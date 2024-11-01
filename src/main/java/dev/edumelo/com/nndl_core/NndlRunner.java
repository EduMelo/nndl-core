package dev.edumelo.com.nndl_core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.step.RunStopperException;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.BrowserControllerDriverConfiguration;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class NndlRunner {
	private static final Logger log = LoggerFactory.getLogger(NndlRunner.class);
	
	private static final String ENTRY_STEP_TAG = "entryStep";
	private static final String ASYNCHRONOUS_STEPS_TAG = "asynchronousSteps";

	private final BrowserControllerDriverConfiguration browserControllerDriverConfiguration;
	private SeleniumSndlWebDriver webDriver;
	private SeleniumSndlWebDriverWaiter webDriverWait;
	
	public NndlRunner(BrowserControllerDriverConfiguration browserControllerDriverConfiguration) {
		this.browserControllerDriverConfiguration = browserControllerDriverConfiguration;
	}

	public NndlResult runFromFile(String sndlFile, ContextAdapter... adapters) {
		try {
			ThreadLocalManager.setAdapters(Arrays.asList(adapters));
			String yamlString = new NndlParser().getYamlString(sndlFile);
			Map<String, Object> yamlStack = new NndlParser().getYamlStack(yamlString);		
			return runStack(yamlStack);
		} finally {
			ThreadLocalManager.expireSession();
		}
	}
	
	public NndlResult runFromStack(ArrayList<String> nndlStack, ContextAdapter... adapters) {
		try {
			ThreadLocalManager.setAdapters(Arrays.asList(adapters));
			Map<String, Object> yamlStack = new NndlParser().getYamlStack(nndlStack);		
			return runStack(yamlStack);			
		} finally {
			ThreadLocalManager.expireSession();
		}
	}

	private NndlResult runStack(Map<String, Object> yamlStack) {
		@SuppressWarnings("unchecked")		
		Map<String, Step> steps = instantiateSteps(browserControllerDriverConfiguration
				.getProperties(), (List<Map<String, ?>>) yamlStack.get(Step.getTag()));
		Collection<String> asynchronousStepsNames = getAsynchronousStepNames(yamlStack);
		try {
			webDriver = new SeleniumSndlWebDriver(browserControllerDriverConfiguration, ThreadLocalManager
					.getBrowserArgumentsContextAdapter());
			webDriverWait = new SeleniumSndlWebDriverWaiter(webDriver);
			webDriver.refreshWebDriver();
			webDriverWait.refreshWaiter();			
			ThreadLocalManager.setWebDriverSessionId(webDriver.getSessionId());

			StepRunner stepRunner = new StepRunner(webDriver, webDriverWait);
			
			if(asynchronousStepsNames != null) {
				Map<String, Step> asynchronousSteps = extractedAsynchronousSteps(steps,
						asynchronousStepsNames);			
				stepRunner.runAsynchronousSteps(null, asynchronousSteps);
			}
			stepRunner.runSteps((String) yamlStack.get(ENTRY_STEP_TAG), steps);
			
			NndlResult result = new NndlResult(ThreadLocalManager.getExtractedData());		
			return result;
		} catch(RunStopperException e) {
			log.debug("Received advice to stop the run");
			NndlResult result = new NndlResult(ThreadLocalManager.getExtractedData());		
			return result;
		} finally {
			if(webDriver != null) {
				webDriver.quitWebDriver();				
			}
			ThreadLocalManager.expireSession();
		}
	}
	
	@SuppressWarnings("unchecked")
	private Collection<String> getAsynchronousStepNames(Map<String, Object> yaml) {
		Object asynchronousStepNames = yaml.get(ASYNCHRONOUS_STEPS_TAG);
		if(asynchronousStepNames instanceof String) {
			return ImmutableSet.of((String) asynchronousStepNames);
		} else if(asynchronousStepNames instanceof List) {
			return (List<String>) asynchronousStepNames;
		}
		
		return null;
	}
	
	private Map<String, Step> extractedAsynchronousSteps(Map<String, Step> steps, Collection<String> asynchronousStepsNames) {
		Map<String, Step> asynchronousSteps = new HashMap<String, Step>();
		
		for (String stepName : asynchronousStepsNames) {
			asynchronousSteps.put(stepName, steps.remove(stepName));
		}
		
		return asynchronousSteps;
	}

	private Map<String, Step> instantiateSteps(SeleniumHubProperties seleniumHubProperties,
			List<Map<String, ?>> stepList) {
		return stepList.stream()
				.map(s -> new Step(seleniumHubProperties, s))
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}

}

package dev.edumelo.com.nndl_core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;
import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;
import dev.edumelo.com.nndl_core.step.RunStopperException;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.BrowserControllerDriverConfiguration;
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

	public NndlResult run(String sndlFile, ContextAdapter... adapters) {
		String sessionId = UUID.randomUUID().toString();
		ContextAdapterHandler.createContext(sessionId, adapters);
						
		String yamlString = new NndlParser().getYamlString(sessionId, sndlFile);
		Map<String, Object> yamlStack = new NndlParser().getYamlStack(sessionId, yamlString);
		run(sessionId, yamlStack);
		NndlResult result = new NndlResult(ContextAdapterHandler.getExtractedData(sessionId));
		ContextAdapterHandler.expireSession(sessionId);
		return result;
	}
	
	public NndlResult runFromStack(ArrayList<String> nndlStack, ContextAdapter... adapters) {
		String sessionId = UUID.randomUUID().toString();
		ContextAdapterHandler.createContext(sessionId, adapters);
		
		Map<String, Object> yamlStack = new NndlParser().getYamlStack(sessionId, nndlStack);
		run(sessionId, yamlStack);
		NndlResult result = new NndlResult(ContextAdapterHandler.getExtractedData(sessionId));
		ContextAdapterHandler.expireSession(sessionId);
		return result;
	}

	private void run(String sessionId, Map<String, Object> yamlStack) {
		@SuppressWarnings("unchecked")
		Map<String, Step> steps = instantiateSteps((List<Map<String, ?>>) yamlStack.get(Step.getTag()));
		Collection<String> asynchronousStepsNames = getAsynchronousStepNames(yamlStack);

		try {
			webDriver = new SeleniumSndlWebDriver(browserControllerDriverConfiguration);
			webDriverWait = new SeleniumSndlWebDriverWaiter(webDriver);
			webDriver.refreshWebDriver();
			webDriverWait.refreshWaiter();
			
			StepRunner stepRunner = new StepRunner(sessionId, webDriver, webDriverWait);
			if(asynchronousStepsNames != null) {
				Map<String, Step> asynchronousSteps = extractedAsynchronousSteps(steps, asynchronousStepsNames);			
				stepRunner.runAsynchronousSteps(null, asynchronousSteps);
			}
			stepRunner.runSteps((String) yamlStack.get(ENTRY_STEP_TAG), steps);
		} catch(RunStopperException e) {
			log.debug("Received advice to stop the run");
		} finally {
			if(webDriver != null) {
				webDriver.quitWebDriver();				
			}
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

	private Map<String, Step> instantiateSteps(List<Map<String, ?>> stepList) {
		return stepList.stream()
				.map(Step::new)
				.collect(Collectors.toMap(Step::getName, Function.identity()));
	}

}

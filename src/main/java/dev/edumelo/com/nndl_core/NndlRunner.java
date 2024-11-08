package dev.edumelo.com.nndl_core;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.nndl.Nndl;
import dev.edumelo.com.nndl_core.step.RunStopperException;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.BrowserControllerDriverConfiguration;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class NndlRunner {
	private static final Logger log = LoggerFactory.getLogger(NndlRunner.class);
	

	private final BrowserControllerDriverConfiguration browserControllerDriverConfiguration;
	private SeleniumSndlWebDriver webDriver;
	private SeleniumSndlWebDriverWaiter webDriverWait;
	
	public NndlRunner(BrowserControllerDriverConfiguration browserControllerDriverConfiguration) {
		this.browserControllerDriverConfiguration = browserControllerDriverConfiguration;
	}

	public NndlResult runFromFile(String sndlFile, ContextAdapter... adapters) {
		Nndl nndl = new Nndl(sndlFile);
		return runFromStack(nndl, adapters);
	}
	
	public NndlResult runFromStack(Nndl nndl, ContextAdapter... adapters) {
		try {
			ThreadLocalManager.setAdapters(Arrays.asList(adapters));
			nndl.loadSteps(ThreadLocalManager.getVariableSubstitutionMap());
			return runStack(nndl);			
		} finally {
			ThreadLocalManager.expireSession();
		}
	}

	private NndlResult runStack(Nndl nndl) {
		SeleniumHubProperties seleniumHubProperties = browserControllerDriverConfiguration
				.getProperties();
		Map<String, Step> steps = nndl.getStepMap(seleniumHubProperties);
		try {
			webDriver = new SeleniumSndlWebDriver(browserControllerDriverConfiguration, ThreadLocalManager
					.getBrowserArgumentsContextAdapter());
			webDriverWait = new SeleniumSndlWebDriverWaiter(webDriver);
			webDriver.refreshWebDriver();
			webDriverWait.refreshWaiter();			
			ThreadLocalManager.setWebDriverSessionId(webDriver.getSessionId());

			StepRunner stepRunner = new StepRunner(webDriver, webDriverWait);
			
			if(nndl.hasAsyncSteps()) {
				Map<String, Step> asynchronousSteps = nndl.getAsyncStepMap(seleniumHubProperties);			
				stepRunner.runAsynchronousSteps(null, asynchronousSteps);
			}
			stepRunner.runSteps(nndl.getEntryStep(), steps);
			
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
	
//	@SuppressWarnings("unchecked")
//	private Collection<String> getAsynchronousStepNames(Map<String, Object> yaml) {
//		Object asynchronousStepNames = yaml.get(ASYNCHRONOUS_STEPS_TAG);
//		if(asynchronousStepNames instanceof String) {
//			return ImmutableSet.of((String) asynchronousStepNames);
//		} else if(asynchronousStepNames instanceof List) {
//			return (List<String>) asynchronousStepNames;
//		}
//		
//		return null;
//	}
	
//	private Map<String, Step> extractedAsynchronousSteps(Map<String, Step> steps, Collection<String> asynchronousStepsNames) {
//		Map<String, Step> asynchronousSteps = new HashMap<String, Step>();
//		
//		for (String stepName : asynchronousStepsNames) {
//			asynchronousSteps.put(stepName, steps.remove(stepName));
//		}
//		
//		return asynchronousSteps;
//	}

//	private Map<String, Step> instantiateSteps(SeleniumHubProperties seleniumHubProperties,
//			NndlNode nndlNode) {
//		return nndlNode.stream()
//				.map(s -> new Step(seleniumHubProperties, s))
//				.collect(Collectors.toMap(Step::getName, Function.identity()));
//	}

}

package dev.edumelo.com.nndl_core;

import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;
import dev.edumelo.com.nndl_core.contextAdapter.NndlContext;
import dev.edumelo.com.nndl_core.contextAdapter.NndlContextManager;
import dev.edumelo.com.nndl_core.nndl.Nndl;
import dev.edumelo.com.nndl_core.step.RunStopperException;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.BrowserControllerDriverConfiguration;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriverWaiter;

public class NndlRunner {
	private static final Logger log = LoggerFactory.getLogger(NndlRunner.class);
	

	private final BrowserControllerDriverConfiguration browserControllerDriverConfiguration;
	private SeleniumNndlWebDriver webDriver;
	private SeleniumNndlWebDriverWaiter webDriverWait;
	
	public NndlRunner(BrowserControllerDriverConfiguration browserControllerDriverConfiguration) {
		this.browserControllerDriverConfiguration = browserControllerDriverConfiguration;
	}

	public NndlResult runFromFile(String nndlFile, ContextAdapter... adapters) {
		Nndl nndl = new Nndl(nndlFile);
		return runFromStack(nndl, adapters);
	}

	public NndlResult runFromDhall(Path nndlFile, ContextAdapter... adapters) {
		return runFromStack(Nndl.loadDhall(nndlFile), adapters);
	}
	
	public NndlResult runFromStack(Nndl nndl, ContextAdapter... adapters) {
		return NndlContextManager.callWithContext(NndlContext.of(nndl.getName(), adapters),
				() -> runWithLoadedStack(nndl));
	}

	private NndlResult runWithLoadedStack(Nndl nndl) {
		//track2
		nndl.loadSteps(NndlContextManager.getVariableSubstitutionMap());
		return runStack(nndl);
	}

	private NndlResult runStack(Nndl nndl) {
		SeleniumHubProperties seleniumHubProperties = browserControllerDriverConfiguration
				.getProperties();
		Map<String, Step> steps = nndl.getStepMap(seleniumHubProperties);
		try {
			webDriver = new SeleniumNndlWebDriver(browserControllerDriverConfiguration, NndlContextManager
					.getBrowserArgumentsContextAdapter());
			webDriverWait = new SeleniumNndlWebDriverWaiter(webDriver);
			webDriver.refreshWebDriver();
			webDriverWait.refreshWaiter();			
			NndlContextManager.setWebDriverSessionId(webDriver.getSessionId());

			StepRunner stepRunner = new StepRunner(webDriver, webDriverWait);
			
			if(nndl.hasAsyncSteps()) {
				Map<String, Step> asynchronousSteps = nndl.getAsyncStepMap(seleniumHubProperties);			
				stepRunner.runAsynchronousSteps(null, asynchronousSteps);
			}
			stepRunner.runSteps(nndl.getEntryStep(), steps);
			
			return new NndlResult(NndlContextManager.getExtractedData());
		} catch(RunStopperException e) {
			log.debug("Received advice to stop the run");
			return new NndlResult(NndlContextManager.getExtractedData());
		} finally {
			if(webDriver != null) {
				webDriver.quitWebDriver();				
			}
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

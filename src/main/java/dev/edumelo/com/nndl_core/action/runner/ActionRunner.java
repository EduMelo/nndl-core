package dev.edumelo.com.nndl_core.action.runner;

import java.util.Collection;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.landmark.LandMarkWaiter;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class ActionRunner {
	
	private final SeleniumSndlWebDriver webDriver;
	private final SeleniumSndlWebDriverWaiter webDriverWait;
	private LandMarkWaiter landmarkWaiter;

	public ActionRunner(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			Collection<ExtractDataBind> extractDataBindList) {
		this.webDriver = remoteWebDriver;
		this.webDriverWait = webDriverWait;
		this.landmarkWaiter = new LandMarkWaiter(remoteWebDriver, webDriverWait);
	}
	
	public Advice run(IterationContent rootElement, Action action) throws NndlActionException {
		Advice advice = null;
		
		if(action instanceof LandmarkConditionAction) {
			advice = ((LandmarkConditionAction) action).runSequentialWait(webDriver, webDriverWait, landmarkWaiter, 
					rootElement);
		} else {
			advice = action.run(webDriver, webDriverWait, rootElement);			
		}
		
		return advice;
	}
	
}

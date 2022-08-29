package dev.edumelo.com.nndl_core.action;

import java.util.Collection;

import dev.edumelo.com.nndl_core.DataBindExtractor;
import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.action.landmark.LandMarkWaiter;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class ActionRunner {
	
	private final SeleniumSndlWebDriver webDriver;
	private final SeleniumSndlWebDriverWaiter webDriverWait;
	private Collection<ExtractDataBind> extractDataBindList;
	private LandMarkWaiter landmarkWaiter;
	private String sessionId;

	public ActionRunner(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait,
			Collection<ExtractDataBind> extractDataBindList) {
		this.sessionId = sessionId;
		this.webDriver = remoteWebDriver;
		this.webDriverWait = webDriverWait;
		this.extractDataBindList = extractDataBindList;
		this.landmarkWaiter = new LandMarkWaiter(remoteWebDriver, webDriverWait);
	}

	public Collection<ExtractDataBind> getExtractDataBindList() {
		return extractDataBindList;
	}
	
	public Advice run(IterationContent rootElement, Action action) throws ActionException {
		Advice advice = null;
		
		if(action instanceof LandmarkConditionAction) {
			advice = ((LandmarkConditionAction) action).runSequentialWait(sessionId, webDriver,
					webDriverWait, landmarkWaiter, rootElement);
		} else {
			advice = action.run(sessionId, webDriver, webDriverWait, rootElement);			
		}
		
		if(action instanceof DataBindExtractor) {
			Collection<ExtractDataBind> extractData = ((DataBindExtractor) action).getExtractDataBind();
			extractDataBindList.addAll(extractData);
		}
		
		return advice;
	}
	
}

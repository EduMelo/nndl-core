package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionException;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public abstract class LandmarkConditionAction extends Action {
	
	private Logger log = LoggerFactory.getLogger(LandmarkConditionAction.class);

	private LandmarkConditionAggregation landmarkConditionAggregation;
	
	public LandmarkConditionAction(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		super(mappedAction, mappedElements);
	}

	public LandmarkConditionAggregation getLandmarkConditionAggregation() {
		return landmarkConditionAggregation;
	}

	protected void setLandMarkConditionAgregation(Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		this.landmarkConditionAggregation = LandmarkConditionAggregation.createLandmarkConditionAggregation(mappedElements, mappedAction);
	}
	
	private Advice wait(LandMarkWaiter landmarkWaiter, LandmarkConditionAggregation landmarkConditionAggregation) throws LandmarkException {
		log.debug("wait: landmarkConditionAggregation: {}", landmarkWaiter, landmarkConditionAggregation);
		return landmarkWaiter.wait(landmarkConditionAggregation);
	}

	public Advice runSequentialWait(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, 
			LandMarkWaiter landmarkWaiter, IterationContent rootElement) throws ActionException {
		log.debug("runSequentialWait: rootElement:{}", rootElement);	
		this.run(sessionId, webDriver, webDriverWait, rootElement);
		return wait(landmarkWaiter, landmarkConditionAggregation);
	}

	
	public void runPrecedentWait(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, 
			LandMarkWaiter landmarkWaiter, IterationContent rootElement) throws ActionException {
		log.debug("runPrecedentWait: rootElement:{}", rootElement);
		wait(landmarkWaiter, landmarkConditionAggregation);
		this.run(sessionId, webDriver, webDriverWait, rootElement);
	}
	
}

package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.exceptions.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.NndlLandmarkException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public abstract class LandmarkConditionAction extends Action {
	
	private Logger log = LoggerFactory.getLogger(LandmarkConditionAction.class);

	private LandmarkConditionAggregation landmarkConditionAggregation;
	private NndlNode action;
	
	public LandmarkConditionAction(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		mappedAction = action;
	}

	public LandmarkConditionAggregation getLandmarkConditionAggregation() {
		return landmarkConditionAggregation;
	}

	protected void setLandMarkConditionAgregation(NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		this.landmarkConditionAggregation = LandmarkConditionAggregation.createLandmarkConditionAggregation(
				mappedElements, mappedAction);
	}
	
	private Advice wait(LandMarkWaiter landmarkWaiter, LandmarkConditionAggregation landmarkConditionAggregation) 
			throws NndlLandmarkException {
		log.debug("wait: landmarkConditionAggregation: {}", landmarkWaiter, landmarkConditionAggregation);
		return landmarkWaiter.wait(landmarkConditionAggregation, getRelevantNode());
	}

	public Advice runSequentialWait(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, 
			LandMarkWaiter landmarkWaiter, IterationContent rootElement) throws NndlActionException {
		log.debug("runSequentialWait: rootElement:{}", rootElement);	
		run(webDriver, webDriverWait, rootElement);
		return wait(landmarkWaiter, landmarkConditionAggregation);
	}

	
	public void runPrecedentWait(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, 
			LandMarkWaiter landmarkWaiter, IterationContent rootElement) throws NndlActionException {
		log.debug("runPrecedentWait: rootElement:{}", rootElement);
		wait(landmarkWaiter, landmarkConditionAggregation);
		this.run(webDriver, webDriverWait, rootElement);
	}
	
}

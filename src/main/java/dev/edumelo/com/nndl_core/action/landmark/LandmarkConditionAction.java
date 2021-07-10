package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Map;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionException;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.Getter;

@Getter
//Retornar
//@Slf4j
public abstract class LandmarkConditionAction extends Action {

	private LandmarkConditionAggregation landmarkConditionAggregation;
	
	protected void setLandMarkConditionAgregation(Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		this.landmarkConditionAggregation = LandmarkConditionAggregation.createLandmarkConditionAggregation(mappedElements, mappedAction);
	}
	
	private Advice wait(LandMarkWaiter landmarkWaiter, LandmarkConditionAggregation landmarkConditionAggregation) throws LandmarkException {
		//XXX Retornar
//		log.debug("wait: landmarkConditionAggregation: {}", landmarkWaiter, landmarkConditionAggregation);
		return landmarkWaiter.wait(landmarkConditionAggregation);
	}

	public Advice runSequentialWait(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, 
			LandMarkWaiter landmarkWaiter, IterationContent rootElement) throws ActionException {
		//XXX Retornar
//		log.debug("runSequentialWait: rootElement:{}", rootElement);	
		this.run(webDriver, webDriverWait, rootElement);
		return wait(landmarkWaiter, landmarkConditionAggregation);
	}

	
	public void runPrecedentWait(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, 
			LandMarkWaiter landmarkWaiter, IterationContent rootElement) throws ActionException {
		//XXX Retornar
//		log.debug("runPrecedentWait: rootElement:{}", rootElement);
		wait(landmarkWaiter, landmarkConditionAggregation);
		this.run(webDriver, webDriverWait, rootElement);
	}
	
}

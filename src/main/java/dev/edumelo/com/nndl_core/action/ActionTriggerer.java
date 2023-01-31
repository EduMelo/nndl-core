package dev.edumelo.com.nndl_core.action;

import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.utils.SpecialParamsTranslater;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import static java.util.stream.Collectors.*;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class ActionTriggerer extends LandmarkConditionAction {
	
	private static final String TAG = "actionTriggerer";
	private static final Object TRIGGER_PARAMS_TAG = "triggerParam";
	private String[] triggerParams;
	
	public ActionTriggerer(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Map<String, ?> mappedActionTrigger = (Map<String, ?>) mappedAction.get(TAG);
		triggerParams = getTriggerParams(mappedActionTrigger);
		setLandMarkConditionAgregation(mappedAction, mappedElements);
	}
	
	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return true;
	}
	
	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		return runElement(sessionId, webDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		return runElement(sessionId, webDriver, webDriverWait, null);
	}
	
	public Advice runElement(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		String[] translatedParams = Arrays.stream(triggerParams)
				.map(p -> SpecialParamsTranslater.translateIfSpecial(sessionId, p))
				.collect(toList())
				.toArray(new String[0]);
				
		ContextAdapterHandler.triggerAction(sessionId, translatedParams);
		
		setActionPerformed(true);			
		return new ContinueAdvice();
	}
	
	private String[] getTriggerParams(Map<String, ?> mappedActionTrigger) {
		Object params = mappedActionTrigger.get(TRIGGER_PARAMS_TAG);
		if(params == null) {
			return null;
		}
		List<String> listParams = ((List<String>) params);
		
		return listParams.toArray(new String[0]);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub

	}
	
	

}

package dev.edumelo.com.nndl_core.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

@SuppressWarnings("unchecked")
public class ActionTriggerer extends Action {
	private static final String TAG = "actionTriggerer";
	private static final String TRIGGER_TAG = "trigger";
	private static final Object TRIGGER_PARAMS_TAG = "triggerParam";
	private Class<ActionTrigger> actionTrigger;
	private String[] triggerParams;
	
	public ActionTriggerer(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Map<String, ?> mappedActionTrigger = (Map<String, ?>) mappedAction.get(TAG);
		actionTrigger = getActionTriggerClass(mappedActionTrigger);
		triggerParams = getTriggerParams(mappedActionTrigger);
	}

	private String[] getTriggerParams(Map<String, ?> mappedActionTrigger) {
		Object params = mappedActionTrigger.get(TRIGGER_PARAMS_TAG);
		if(params == null) {
			return null;
		}
		return ((List<String>) params).toArray(new String[0]);
	}

	private Class<ActionTrigger> getActionTriggerClass(Map<String, ?> mappedActionTrigger) {
		String className = (String) mappedActionTrigger.get(TRIGGER_TAG);
		
		try {
			return (Class<ActionTrigger>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			String message = String.format("Cannot found class by the name: %s", className);
			throw new RuntimeException(message);
		}
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public boolean isIgnoreRoot() {
		return false;
	}
	
	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		return runElement(webDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		return runElement(webDriver, webDriverWait, null);
	}
	
	public Advice runElement(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) {
		if(checkCondition(webDriver, webDriverWait, rootElement)) {
			ActionTrigger trigger;
			try {
				trigger = actionTrigger.getConstructor().newInstance();
			} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
				String message = "Error while instantiating Storer class";
				throw new RuntimeException(message, e);
			}
			List<Object> params = new ArrayList<>();
			params.add(rootElement);
			if(triggerParams != null) {
				params.addAll(Arrays.asList(triggerParams));				
			}
			
			trigger.triggerAction(params.toArray());
			setActionPerformed(true);			
		} else {
			setActionPerformed(false);
		}
		return new ContinueAdvice();
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub

	}

}

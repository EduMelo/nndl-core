package dev.edumelo.com.nndl_core.action.impl.triggerer;

import static dev.edumelo.com.nndl_core.action.ElementWaitCondition.NONE;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Map;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.ElementWaitCondition;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.utils.SpecialParamsTranslater;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class ActionTriggerer extends LandmarkConditionAction {
	private static final String TAG = "actionTriggerer";
	private static final String ID_TAG = "id";
	private static final String TRIGGER_PARAMS_TAG = "triggerParam";
	private String triggerId;
	private String[] triggerParams;
	private NndlNode relevantNode;
	
	public ActionTriggerer(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		NndlNode mappedActionTrigger = mappedAction.getValueFromChild(TAG).orElseThrow(NndlParserRuntimeException
				.get("Tag LandmarkConditionAction should have "+TAG+" tag", mappedAction));
		triggerId = getTriggerId(mappedActionTrigger);
		triggerParams = getTriggerParams(mappedActionTrigger);
		this.relevantNode = mappedAction;
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
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}
	
	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		return runElement(webDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws NndlActionException {
		return runElement(webDriver, webDriverWait, null);
	}
	
	@Override
	public ElementWaitCondition getDefaultWaitCondition() {
		return NONE;
	}
	
	@Override
	public StepElement getRelevantElment() {
		return null;
	}
	
	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
	}
	
	public Advice runElement(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {

		Object[] translatedParams = Arrays.stream(triggerParams)
				.map(p -> SpecialParamsTranslater.translateIfSpecial(p))
				.collect(toList())
				.toArray(new String[0]);
				
		ThreadLocalManager.triggerAction(triggerId, translatedParams);
		
		setActionPerformed(true);			
		return new ContinueAdvice();
	}
	
	private String getTriggerId(NndlNode mappedActionTrigger) {
		return mappedActionTrigger.getScalarValueFromChild(ID_TAG).orElseThrow(NndlParserRuntimeException
				.get("Action ActionTriggerer should have "+ID_TAG+" tag.", mappedActionTrigger));
	}
	
	private String[] getTriggerParams(NndlNode mappedActionTrigger) {
		return mappedActionTrigger.getListedValuesFromChild(TRIGGER_PARAMS_TAG)
				.get()
				.stream()
				.map(NndlNode::getScalarValue)
				.toArray(String[]::new);
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;


import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriverWaiter;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;

public class Hover extends LandmarkConditionAction {

private static final String TAG = "hover";
	private StepElement hoverableElement;
	private NndlNode relevantNode;
	
	public Hover(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.hoverableElement = getElement(mappedAction, mappedElements);
		this.relevantNode = mappedAction;
		setLandMarkConditionAgregation(mappedAction, mappedElements);
	}
	
	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return hoverableElement.isIgnoreRoot();
	}
	
	@Override
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}
	
	@Override
	public LandmarkStrategies getDefaultWaitCondition() {
		return new LandmarkStrategies(LandmarkAchievementStrategy.IS_CLICKABLE);
	}
	
	@Override
	public StepElement getRelevantElment() {
		return this.hoverableElement;
	}

	@Override
	public Advice runNested(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		return runElement(webDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(NndlWebDriver remoteWebDriver, NndlWebDriverWaiter webDriverWait)
			throws NndlActionException {	
		return runElement(remoteWebDriver, webDriverWait, null);
	}
	
	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub	
	}
	
	public Advice runElement(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait, 
			IterationContent rootElement) throws NndlActionException {
		NndlElement element = wait(webDriver, webDriverWait);
		if(checkCondition(webDriver, webDriverWait, rootElement)) {
			webDriver.hover(element);
		}
		
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	private StepElement getElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		String elementKey = mappedAction.getScalarValueFromChild(TAG).orElseThrow(() -> new NndlParserRuntimeException(
				"Hover Action should have "+TAG+" tag.", mappedAction));
		return mappedElements.get(elementKey);
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

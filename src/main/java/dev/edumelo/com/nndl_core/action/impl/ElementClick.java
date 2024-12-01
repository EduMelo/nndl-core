package dev.edumelo.com.nndl_core.action.impl;

import static dev.edumelo.com.nndl_core.action.ElementWaitCondition.PRESENT;

import java.util.Map;

import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.ElementWaitCondition;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class ElementClick extends LandmarkConditionAction {
	private static final String TAG = "elementClick";
	private StepElement clickableElement;
	private NndlNode relevantNode;
	
	public ElementClick(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.clickableElement = getElement(mappedAction, mappedElements);
		this.relevantNode = mappedAction;
		setLandMarkConditionAgregation(mappedAction, mappedElements);
	}

	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return clickableElement.isIgnoreRoot();
	}
	
	@Override
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}
	
	@Override
	public ElementWaitCondition getDefaultWaitCondition() {
		return PRESENT;
	}
	
	@Override
	public StepElement getRelevantElment() {
		return this.clickableElement;
	}

	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		WebElement button = wait(webDriver, webDriverWait);
		return runElement(webDriver, webDriverWait, rootElement, button);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait)
			throws NndlActionException {	
		WebElement button = wait(webDriver, webDriverWait);
		return runElement(webDriver, webDriverWait, null, button);
	}
	
	public Advice runElement(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement, WebElement button) {
		if(checkCondition(webDriver, webDriverWait, rootElement)) {
			button.click();
			setActionPerformed(true);
		} else {
			setActionPerformed(false);
		}
		
		return new ContinueAdvice();
	}

	private StepElement getElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		String elementKey = mappedAction.getScalarValueFromChild(TAG).orElseThrow(NndlParserRuntimeException
				.get("Action ElementClick should have "+TAG+" tag.", mappedAction));
		return mappedElements.get(elementKey);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

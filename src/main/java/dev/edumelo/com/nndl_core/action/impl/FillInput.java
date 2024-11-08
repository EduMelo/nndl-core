package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.exceptions.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class FillInput extends Action {
	private static final String TAG = "input";
	private static final String VALUE_TAG = "value";
	private StepElement inputElement;
	private String value;
	private NndlNode relevantNode;
	
	public FillInput(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.inputElement = getElement(mappedAction, mappedElements);
		this.value = getValue(mappedAction);
		this.relevantNode = mappedAction;
	}
	
	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return inputElement.isIgnoreRoot();
	}
	
	@Override
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}

	@Override
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) {
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(inputElement.elementToBeClickable(remoteWebDriver));
		return runElement(input);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {	
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(inputElement.elementToBeClickable(remoteWebDriver));		
		
		setActionPerformed(true);
		return runElement(input);
	}
	
	public Advice runElement(WebElement input) {
		input.sendKeys(this.value);
		return new ContinueAdvice();
	}
	
	private String getValue(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(VALUE_TAG).orElseThrow(NndlParserRuntimeException
				.get("Action FillInput should have "+VALUE_TAG+" tag", mappedAction));
	}

	private StepElement getElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		String elementKey = mappedAction.getScalarValueFromChild(TAG).orElseThrow(NndlParserRuntimeException
				.get("Action FillInput should have "+TAG+" tag.", mappedAction));
		return mappedElements.get(elementKey);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "FillInput [inputElement=" + inputElement + ", value=" + value + "]";
	}
}

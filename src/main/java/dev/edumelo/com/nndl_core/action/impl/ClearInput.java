package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.exceptions.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class ClearInput extends Action {
	private static final String TAG = "input";
	private StepElement inputElement;
	private NndlNode relevantNode;

	public ClearInput(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.inputElement = getElement(mappedAction, mappedElements);
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
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub

	}

	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		//TODO parametrized duration
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(ExpectedConditions.elementToBeClickable(
				rootElement.getRootElement().findElement(inputElement.getLocator(webDriver))));
		return runElement(input);
	}

	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait)
			throws NndlActionException {
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(ExpectedConditions.elementToBeClickable(
				inputElement.getLocator(webDriver)));		
		
		setActionPerformed(true);
		return runElement(input);
	}
	
	private StepElement getElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getScalarValueFromChild(TAG)
				.map(mappedElements::get)
				.orElseThrow(NndlParserRuntimeException.get("Action ClearInput should have "+TAG+" tag.", mappedAction));
	}
	
	private Advice runElement(WebElement input) {
		input.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
		return new ContinueAdvice();
	}

}
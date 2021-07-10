package dev.edumelo.com.nndl_core.action;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.ToString;

@ToString
public class FillInput extends Action {
	private static final String TAG = "input";
	private static final String VALUE_TAG = "value";
	private StepElement inputElement;
	private String value;
	
	public FillInput(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		this.inputElement = getElement(mappedAction, mappedElements);
		this.value = getValue(mappedAction);
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
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		//TODO parametrized duration
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50)).until(ExpectedConditions.elementToBeClickable(
				rootElement.getRootElement().findElement(inputElement.getLocator(remoteWebDriver))));
		return runElement(input);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {	
		WebElement input =  webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50)).until(ExpectedConditions.elementToBeClickable(
				inputElement.getLocator(remoteWebDriver)));		
		
		setActionPerformed(true);
		return runElement(input);
	}
	
	public Advice runElement(WebElement input) {
		input.sendKeys(this.value);
		return new ContinueAdvice();
	}
	
	private String getValue(Map<String, ?> mappedAction) {
		return (String) mappedAction.get(VALUE_TAG);
	}

	private StepElement getElement(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		String elementKey = (String) mappedAction.get(TAG);
		return mappedElements.get(elementKey);
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}
}

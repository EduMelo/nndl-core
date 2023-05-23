package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Hover extends LandmarkConditionAction {

private static final String TAG = "hover";
	private StepElement hoverableElement;
	
	public Hover(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		super(mappedAction, mappedElements);
		this.hoverableElement = getElement(mappedAction, mappedElements);
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
	public Advice runNested(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		ExpectedCondition<WebElement> expectedCondition = ExpectedConditions.elementToBeClickable(
				rootElement.getRootElement().findElement(hoverableElement
						.getLocator(remoteWebDriver)));
		WebElement element  = webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(expectedCondition);
		return runElement(remoteWebDriver, webDriverWait, element, rootElement, expectedCondition);
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) {	
		ExpectedCondition<WebElement> expectedCondition = ExpectedConditions
				.visibilityOfElementLocated(
				hoverableElement.getLocator(remoteWebDriver));
		WebElement element  = webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(expectedCondition);
		return runElement(remoteWebDriver, webDriverWait, element, null, expectedCondition);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, 
			WebElement element, IterationContent rootElement, ExpectedCondition<WebElement> expectedCondition) {
		
//		positionBefore(remoteWebDriver, webDriverWait, element, expectedCondition);
		Actions actions = new Actions(remoteWebDriver.getWebDriver());
		if(checkCondition(remoteWebDriver, webDriverWait, rootElement)) {
			actions.moveToElement(element).perform();
		}
//		positionAfter(remoteWebDriver, webDriverWait, element, expectedCondition);
		
		setActionPerformed(true);
		return new ContinueAdvice();
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

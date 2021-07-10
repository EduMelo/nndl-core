package dev.edumelo.com.nndl_core.action;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.ToString;

@ToString
public class Click extends LandmarkConditionAction {
	private static final String TAG = "elementClick";
	private StepElement clickableElement;
	
	public Click(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		this.clickableElement = getElement(mappedAction, mappedElements);
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
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {	
		//TODO parametrized duration
		WebElement button = webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50)).until(ExpectedConditions.elementToBeClickable(
				rootElement.getRootElement().findElement(clickableElement.getLocator(remoteWebDriver))));
		return runElement(button);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {	
		//TODO parametrized duration
		WebElement button = webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50)).until(ExpectedConditions.elementToBeClickable(
					clickableElement.getLocator(remoteWebDriver)));			
		return runElement(button);
	}
	
	public Advice runElement(WebElement button) {
		if(checkCondition(button)) {
			button.click();
			setActionPerformed(true);
		} else {
			setActionPerformed(false);
		}
		
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

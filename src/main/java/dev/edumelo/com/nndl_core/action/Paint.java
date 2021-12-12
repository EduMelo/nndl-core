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

public class Paint extends Action {
	private static final String TAG = "elementPaint";
	private static final String COLOR_TAG = "color";
	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private StepElement paintableElement;
	private String color;
	private boolean ignoreRoot;

	@Override
	public String getTag() {
		return TAG;
	}
	
	public Paint(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		paintableElement = getElement(mappedAction, mappedElements);
		color = getColor(mappedAction);
		extractIgnoreRoot(mappedAction, mappedElements);
	}
	
	private String getColor(Map<String, ?> mappedAction) {
		return (String) mappedAction.get(COLOR_TAG);
	}
	
	private StepElement getElement(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		if(mappedElements == null) {
			return null;
		}
		String elementKey = (String) mappedAction.get(TAG);
		return mappedElements.get(elementKey);
	}
	
	private void extractIgnoreRoot(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Object value = mappedAction.get(IGNORE_ROOT_TAG);
		if(value != null) {
			ignoreRoot = (Boolean) value;
		}
		ignoreRoot = false;
	}

	@Override
	public boolean isIgnoreRoot() {
		return ignoreRoot;
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub

	}
	
	private boolean isTargetSpecial(StepElement targetElement) {
		if(targetElement == null || targetElement.getName() == null) {
			return false;
		}
		return targetElement.getName().startsWith("$");
	}

	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws ActionException {
		WebElement target = null;
		if(isTargetSpecial(paintableElement)) {
			target = webDriver.getWebDriver().switchTo().activeElement();
		} else {
			if(paintableElement != null) {
				target = webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50))
						.until(ExpectedConditions.elementToBeClickable(
								rootElement.getRootElement().findElement(paintableElement.getLocator(webDriver))));
			} else {
				target = rootElement.getRootElement();
			}
		}
		Advice advice = runElement(webDriver, webDriverWait, rootElement, target);
		setActionPerformed(true);
		return advice;
	}

	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait)
			throws ActionException {
		WebElement element =  webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50))
				.until(ExpectedConditions.elementToBeClickable(paintableElement.getLocator(webDriver)));
		setActionPerformed(true);
		return runElement(webDriver, webDriverWait, null, element);
	}
	
	public Advice runElement(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement, WebElement element) {
		if(checkCondition(webDriver, webDriverWait, rootElement)) {
			webDriver.getWebDriver().executeScript("arguments[0].style.backgroundColor = arguments[1]", element, color);		
			setActionPerformed(true);
		} else {
			setActionPerformed(false);
		}
		return new ContinueAdvice();
	}

}

package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionException;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class ElementMark extends Action {
	private static final String TAG = "elementMark";
	private static final String MARK_TAG = "mark";
	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private StepElement markableElement;
	private String mark;
	private boolean ignoreRoot;
	
	public ElementMark(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		super(mappedAction);
		markableElement = getElement(mappedAction, mappedElements);
		mark = getMark(mappedAction);
		extractIgnoreRoot(mappedAction, mappedElements);
	}
	
	@Override
	public String getTag() {
		return TAG;
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
	
	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		WebElement target = null;
		if(isTargetSpecial(markableElement)) {
			target = webDriver.getWebDriver().switchTo().activeElement();
		} else {
			if(markableElement != null) {
				target = webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
						.until(ExpectedConditions.elementToBeClickable(
								rootElement.getRootElement().findElement(markableElement.getLocator(webDriver))));
			} else {
				target = rootElement.getRootElement();
			}
		}
		Advice advice = runElement(webDriver, webDriverWait, rootElement, target);
		setActionPerformed(true);
		return advice;
	}


	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		WebElement element =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(ExpectedConditions.presenceOfElementLocated(markableElement
						.getLocator(webDriver)));
		setActionPerformed(true);
		return runElement(webDriver, webDriverWait, null, element);
	}

	private String getMark(Map<String, ?> mappedAction) {
		return (String) mappedAction.get(MARK_TAG);
	}

	
	private StepElement getElement(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		if(mappedElements == null) {
			return null;
		}
		String elementKey = (String) mappedAction.get(TAG);
		return mappedElements.get(elementKey);
	}
	
	private boolean isTargetSpecial(StepElement targetElement) {
		if(targetElement == null || targetElement.getName() == null) {
			return false;
		}
		return targetElement.getName().startsWith("$");
	}

	private Advice runElement(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement, WebElement element) {
		if(checkCondition(webDriver, webDriverWait, rootElement)) {
			webDriver.getWebDriver().executeScript("arguments[0].setAttribute('class', arguments[1])", element, mark);		
			setActionPerformed(true);
		} else {
			setActionPerformed(false);
		}
		return new ContinueAdvice();
	}

}

package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionException;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Extract extends Action {
	private static final String TAG = "extract";
	private static final String TARGET_TAG = "targertElement";
	private static final String FROM_CLIPBOARD_TAG = "fromClipboard";
	private String extractDataBindAdapterName;
	private StepElement targetElement;
	private boolean fromClipboard;
	
	public Extract(SeleniumHubProperties seleniumHubProperties, Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		extractDataBindAdapterName = getExtractoClass(mappedAction);
		targetElement = getTargetElement(mappedAction, mappedElements);
		fromClipboard = getFromClipboard(mappedAction, mappedElements);
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public boolean isIgnoreRoot() {
		return false;
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean getFromClipboard(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Object fromClipboardObject = mappedAction.get(FROM_CLIPBOARD_TAG);
		if(fromClipboardObject != null) {
			Boolean fromClipboard = (Boolean) fromClipboardObject;
			return fromClipboard;
		}
		return false;
	}
	
	private StepElement getTargetElement(Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		Object elementNameObject = mappedAction.get(TARGET_TAG);
		if(elementNameObject != null) {
			String elementName = (String) elementNameObject;
			return mappedElements.get(elementName);
		}
		return null;
	}

	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		if(fromClipboard) {
			return runElementFromClipboard(webDriver, sessionId);
		}
		return runElement(webDriver, sessionId, rootElement.getRootElement());
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		if(fromClipboard) {
			return runElementFromClipboard(webDriver, sessionId);
		}
		WebElement target =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(ExpectedConditions.elementToBeClickable(
				targetElement.getLocator(webDriver)));
		
		return runElement(webDriver, sessionId, target);
	}
	
	private Advice runElementFromClipboard(SeleniumSndlWebDriver webDriver, String sessionId) {
		return ContextAdapterHandler.addExtractedData(webDriver, sessionId,
				ContextAdapterHandler.CLIPBOARD_DATA_BINDER_NAME, null);
	}

	public Advice runElement(SeleniumSndlWebDriver webDriver, String sessionId,
			WebElement element) {
		return ContextAdapterHandler.addExtractedData(webDriver, sessionId,
				extractDataBindAdapterName, element);
	}
	
	private String getExtractoClass(Map<String, ?> mappedAction) {
		return (String) mappedAction.get(TAG);
	}
	

	@Override
	public String toString() {
		return "Extract [extractDataBindAdapterName=" + extractDataBindAdapterName
				+ ", targetElement=" + targetElement + "]";
	}
	
	

}

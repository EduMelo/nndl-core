package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.contextAdapter.ClipboardTextFactory;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.exceptions.ActionException;
import dev.edumelo.com.nndl_core.exceptions.NndlParserException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
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
	
	public Extract(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
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
	
	private boolean getFromClipboard(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getScalarValueFromChild(FROM_CLIPBOARD_TAG, Boolean.class).orElse(false);
	}
	
	private StepElement getTargetElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getScalarValueFromChild(TARGET_TAG)
				.map(mappedElements::get)
				.orElse(null);
	}

	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws ActionException {
		if(fromClipboard) {
			return runElementFromClipboard(webDriver);
		}
		return runElement(webDriver, rootElement.getRootElement());
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		if(fromClipboard) {
			return runElementFromClipboard(webDriver);
		}
		WebElement target =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
				.until(ExpectedConditions.elementToBeClickable(
				targetElement.getLocator(webDriver)));
		
		return runElement(webDriver, target);
	}
	
	private Advice runElementFromClipboard(SeleniumSndlWebDriver webDriver) {
		return ThreadLocalManager.addExtractedData(webDriver, ClipboardTextFactory.class.getName(),
				null);
	}

	public Advice runElement(SeleniumSndlWebDriver webDriver, WebElement element) {
		return ThreadLocalManager.addExtractedData(webDriver, extractDataBindAdapterName, element);
	}
	
	private String getExtractoClass(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(TAG).orElseThrow(NndlParserException
				.get("Action Extract should have "+TAG+" tag.", mappedAction));
	}
	

	@Override
	public String toString() {
		return "Extract [extractDataBindAdapterName=" + extractDataBindAdapterName
				+ ", targetElement=" + targetElement + "]";
	}
	
	

}

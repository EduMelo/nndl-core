package dev.edumelo.com.nndl_core.action;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Extract extends Action {
	private static final String TAG = "extract";
	private static final String TARGET_TAG = "targertElement";
	private String extractDataBindAdapterName;
	private StepElement targetElement;
	
	public Extract(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		extractDataBindAdapterName = getExtractoClass(mappedAction);
		targetElement = getTargetElement(mappedAction, mappedElements);
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

	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		return runElement(webDriver, sessionId, rootElement.getRootElement());
	}

	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		//TODO parametrized duration
		WebElement target =  webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50))
				.until(ExpectedConditions.elementToBeClickable(
				targetElement.getLocator(webDriver)));
		
		return runElement(webDriver, sessionId, target);
	}
	
	public Advice runElement(SeleniumSndlWebDriver webDriver, String sessionId,
			WebElement element) {
		return ContextAdapterHandler.addExtractedData(webDriver, sessionId,
				extractDataBindAdapterName, element);
	}
	
	private String getExtractoClass(Map<String, ?> mappedAction) {
		return (String) mappedAction.get(TAG);
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
	public String toString() {
		return "Extract [extractDataBindAdapterName=" + extractDataBindAdapterName
				+ ", targetElement=" + targetElement + "]";
	}
	
	

}

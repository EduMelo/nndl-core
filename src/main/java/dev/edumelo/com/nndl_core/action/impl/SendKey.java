package dev.edumelo.com.nndl_core.action.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class SendKey extends LandmarkConditionAction {
	private static final String TAG = "sendKey";
	private static final String TARGET_TAG = "targetElement";
	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private CharSequence key;
	private StepElement targetElement;
	private boolean ignoreRoot;
	
	public SendKey(SeleniumHubProperties seleniumHubProperties, Map<String, ?> mappedAction,
			Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.key = getKey(mappedAction, mappedElements);
		targetElement = getTargetElement(mappedAction, mappedElements);
		setLandMarkConditionAgregation(mappedAction, mappedElements);
		extractIgnoreRoot(mappedAction, mappedElements);
	}


	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return ignoreRoot;
	}
	
	@Override
	public Advice runNested(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) {
		WebElement target = null;
		if(isTargetSpecial(targetElement)) {
			target = remoteWebDriver.getWebDriver().switchTo().activeElement();
		} else {
			if(targetElement != null) {
				target =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
						.until(targetElement.elementToBeClickable(remoteWebDriver));
			} else {
				target = rootElement.getRootElement();
			}			
		}
		
		target.sendKeys(Keys.chord(key));
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	private boolean isTargetSpecial(StepElement targetElement) {
		if(targetElement == null || targetElement.getName() == null) {
			return false;
		}
		return targetElement.getName().startsWith("$");
	}


	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) {
		WebElement target = null;
		if(targetElement != null) {
			target =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
					.until(targetElement.elementToBeClickable(remoteWebDriver));
		} else {
			target = runElement(remoteWebDriver);
		}
		
		target.sendKeys(Keys.chord(key));
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	public WebElement runElement(SeleniumSndlWebDriver remoteWebDriver) {
		return remoteWebDriver.getWebDriver().findElement(By.tagName("html"));
	}

	private StepElement getTargetElement(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Object elementNameObject = mappedAction.get(TARGET_TAG);
		if(elementNameObject != null) {
			String elementName = (String) elementNameObject;
			return mappedElements.get(elementName);
		}
		return null;
	}
	 
	
	private CharSequence getKey(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		String value = (String) mappedAction.get(TAG);
	    List<String> keyList = Arrays.asList(Keys.values()).stream()
	    		.map(Keys::name)
	    		.collect(Collectors.toList());
	    
		if(keyList.contains(value)) {
			return Keys.valueOf(value);
		}
		return value;
	}

	private void extractIgnoreRoot(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Object value = mappedAction.get(IGNORE_ROOT_TAG);
		if(value != null) {
			ignoreRoot = (Boolean) value;
		}
		ignoreRoot = false;
	}
	
	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "SendKey [key=" + key + ", targetElement=" + targetElement + ", ignoreRoot=" + ignoreRoot + "]";
	}
}

package dev.edumelo.com.nndl_core.action.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkConditionAction;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
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
	private NndlNode relevantNode;
	
	public SendKey(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.key = getKey(mappedAction, mappedElements);
		this.relevantNode = mappedAction;
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
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}
	
	@Override
	public LandmarkStrategies getDefaultWaitCondition() {
		return new LandmarkStrategies(LandmarkAchievementStrategy.IS_CLICKABLE);
	}
	
	@Override
	public StepElement getRelevantElment() {
		return this.targetElement;
	}
	
	@Override
	public Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		WebElement target = null;
		if(isTargetSpecial(targetElement)) {
			target = webDriver.getWebDriver().switchTo().activeElement();
		} else {
			if(targetElement != null) {
				target = wait(webDriver, webDriverWait);
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
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) {
		WebElement target = null;
		if(targetElement != null) {
			target =  webDriverWait.getWebDriverWaiter().withTimeout(getTimeoutSeconds())
					.until(targetElement.landmarkAchiveable(remoteWebDriver, getWaitCondition()));
		} else {
			target = runElement(remoteWebDriver);
		}
		
		target.sendKeys(Keys.chord(key));
		setActionPerformed(true);
		return new ContinueAdvice();
	}
	
	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
	}
	
	public WebElement runElement(SeleniumSndlWebDriver remoteWebDriver) {
		return remoteWebDriver.getWebDriver().findElement(By.tagName("html"));
	}

	private StepElement getTargetElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getScalarValueFromChild(TARGET_TAG)
				.map(mappedElements::get)
				.orElse(null);
	}
	 
	
	private CharSequence getKey(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		String value = mappedAction.getScalarValueFromChild(TAG)
				.orElseThrow(() -> new NndlParserRuntimeException("SendKey tag should have an "+TAG+" tag.", mappedAction));
	    List<String> keyList = Arrays.asList(Keys.values()).stream()
	    		.map(Keys::name)
	    		.collect(Collectors.toList());
	    
		if(keyList.contains(value)) {
			return Keys.valueOf(value);
		}
		return value;
	}

	private void extractIgnoreRoot(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		ignoreRoot = mappedAction.getScalarValueFromChild(IGNORE_ROOT_TAG, Boolean.class).orElse(false);
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

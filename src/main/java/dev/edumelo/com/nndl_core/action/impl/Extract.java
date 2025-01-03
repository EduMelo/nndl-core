package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;

import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.contextAdapter.ClipboardTextFactory;
import dev.edumelo.com.nndl_core.contextAdapter.ThreadLocalManager;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
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
	private NndlNode relevantNode;
	
	public Extract(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		extractDataBindAdapterName = getExtractoClass(mappedAction);
		targetElement = getTargetElement(mappedAction, mappedElements);
		fromClipboard = getFromClipboard(mappedAction, mappedElements);
		this.relevantNode = mappedAction;
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
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
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
		if(fromClipboard) {
			return runElementFromClipboard(webDriver);
		}
		return runElement(webDriver, rootElement.getRootElement());
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws NndlActionException {
		if(fromClipboard) {
			return runElementFromClipboard(webDriver);
		}
		WebElement target =  wait(webDriver, webDriverWait);
		return runElement(webDriver, target);
	}
	
	private boolean getFromClipboard(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getScalarValueFromChild(FROM_CLIPBOARD_TAG, Boolean.class).orElse(false);
	}
	
	private StepElement getTargetElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction.getScalarValueFromChild(TARGET_TAG)
				.map(mappedElements::get)
				.orElse(null);
	}
	
	private Advice runElementFromClipboard(SeleniumSndlWebDriver webDriver) {
		return ThreadLocalManager.addExtractedData(webDriver, ClipboardTextFactory.class.getName(),
				null);
	}

	public Advice runElement(SeleniumSndlWebDriver webDriver, WebElement element) {
		return ThreadLocalManager.addExtractedData(webDriver, extractDataBindAdapterName, element);
	}
	
	private String getExtractoClass(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(TAG).orElseThrow(NndlParserRuntimeException
				.get("Action Extract should have "+TAG+" tag.", mappedAction));
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}

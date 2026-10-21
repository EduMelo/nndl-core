package dev.edumelo.com.nndl_core.action.impl;

import java.util.Map;


import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.exceptions.unchecked.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriverWaiter;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;

public class ElementPaint extends Action {
	private static final String TAG = "elementPaint";
	private static final String COLOR_TAG = "color";
	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private StepElement paintableElement;
	private String color;
	private boolean ignoreRoot;
	private NndlNode relevantNode;

	@Override
	public String getTag() {
		return TAG;
	}
	
	@Override
	public LandmarkStrategies getDefaultWaitCondition() {
		return new LandmarkStrategies(LandmarkAchievementStrategy.IS_CLICKABLE);
	}
	
	@Override
	public StepElement getRelevantElment() {
		return this.paintableElement;
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
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Advice runNested(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException {
		NndlElement target = null;
		if(isTargetSpecial(paintableElement)) {
			target = webDriver.activeElement();
		} else {
			if(paintableElement != null) {
				target = wait(webDriver, webDriverWait);
			} else {
				target = rootElement.getRootElement();
			}
		}
		Advice advice = runElement(webDriver, webDriverWait, rootElement, target);
		setActionPerformed(true);
		return advice;
	}

	@Override
	public Advice runAction(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait)
			throws NndlActionException {
		NndlElement element =  wait(webDriver, webDriverWait);
		return runElement(webDriver, webDriverWait, null, element);
	}
	
	public Advice runElement(NndlWebDriver webDriver, NndlWebDriverWaiter webDriverWait,
			IterationContent rootElement, NndlElement element) {
		if(checkCondition(webDriver, webDriverWait, rootElement)) {
			webDriver.executeScript("arguments[0].style.backgroundColor = arguments[1]", element, color);		
			setActionPerformed(true);
		} else {
			setActionPerformed(false);
		}
		return new ContinueAdvice();
	}
	
	public ElementPaint(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		paintableElement = getElement(mappedAction, mappedElements);
		color = getColor(mappedAction);
		this.relevantNode = mappedAction;
		extractIgnoreRoot(mappedAction, mappedElements);
	}
	
	private String getColor(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(COLOR_TAG).orElseThrow(NndlParserRuntimeException
				.get("Action ElementPaint shoud have "+COLOR_TAG+" tag", mappedAction));
	}
	
	private StepElement getElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		if(mappedElements == null) {
			return null;
		}
		String elementKey = mappedAction.getScalarValueFromChild(TAG).orElseThrow(NndlParserRuntimeException
				.get("Action ElementPaint should have "+TAG+" tag", mappedAction));
		return mappedElements.get(elementKey);
	}
	
	private void extractIgnoreRoot(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		ignoreRoot =  mappedAction.getScalarValueFromChild(IGNORE_ROOT_TAG, Boolean.class).orElse(false);
	}
	
	private boolean isTargetSpecial(StepElement targetElement) {
		if(targetElement == null || targetElement.getName() == null) {
			return false;
		}
		return targetElement.getName().startsWith("$");
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

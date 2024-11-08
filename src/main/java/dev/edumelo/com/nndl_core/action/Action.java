package dev.edumelo.com.nndl_core.action;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatus;
import dev.edumelo.com.nndl_core.action.utils.Position;
import dev.edumelo.com.nndl_core.exceptions.NndlActionException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public abstract class Action {
	
	private final static Logger log = LoggerFactory.getLogger(Action.class);
	
	private static final String TIMEOUT_TAG = "timeout";
	private static int DEFAULT_TIMEOUT = 50;
	
	private static final String TAG = "actions";
	private int order;
	private int waitDuration;
	private RequirementStatus requirementStatus;
	private Class<ActionCondition> conditionClass;
	private StepElement conditionElement;
	private boolean limitRequirement;
	private boolean actionPerformed;
	private Position positionBefore;
	private Position positionAfter;
	private int onEach;
	private int timeoutSeconds;
	private SeleniumHubProperties seleniumHubProperties;
	
	public abstract String getTag();
	public abstract boolean isIgnoreRoot();
	public abstract NndlNode getRelevantNode();
	public abstract void runPreviousModification(ActionModificator modificiator);
	public abstract Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) throws NndlActionException;
	public abstract Advice runAction(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait)
			throws NndlActionException;
	
	public Action(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, StepElement> mappedElements) {
		this.seleniumHubProperties = seleniumHubProperties;
		timeoutSeconds = mappedAction.getScalarValueFromChild(TIMEOUT_TAG, Integer.class).orElse(getDefaultTimeout());
	}
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getWaitDuration() {
		return waitDuration;
	}
	public void setWaitDuration(int waitDuration) {
		this.waitDuration = waitDuration;
	}
	public RequirementStatus getRequirementStatus() {
		return requirementStatus;
	}
	public void setRequirementStatus(RequirementStatus requirementStatus) {
		this.requirementStatus = requirementStatus;
	}
	public Class<ActionCondition> getConditionClass() {
		return conditionClass;
	}
	public void setConditionClass(Class<ActionCondition> conditionClass) {
		this.conditionClass = conditionClass;
	}
	public StepElement getConditionElement() {
		return conditionElement;
	}
	public void setConditionElement(StepElement conditionElement) {
		this.conditionElement = conditionElement;
	}
	public boolean isLimitRequirement() {
		return limitRequirement;
	}
	public void setLimitRequirement(boolean limitRequirement) {
		this.limitRequirement = limitRequirement;
	}
	public boolean isActionPerformed() {
		return actionPerformed;
	}
	public void setActionPerformed(boolean actionPerformed) {
		this.actionPerformed = actionPerformed;
	}
	public Position getPositionBefore() {
		return positionBefore;
	}
	public void setPositionBefore(Position positionBefore) {
		this.positionBefore = positionBefore;
	}
	public Position getPositionAfter() {
		return positionAfter;
	}
	public void setPositionAfter(Position positionAfter) {
		this.positionAfter = positionAfter;
	}
	public int getOnEach() {
		return onEach;
	}
	public void setOnEach(int onEach) {
		this.onEach = onEach;
	}
	protected SeleniumHubProperties getSeleniumHubProperties() {
		return seleniumHubProperties;
	}
	protected int getDefaultTimeout() {
		return DEFAULT_TIMEOUT;
	}
	
	protected Duration getTimeoutSeconds() {
		return Duration.ofSeconds(timeoutSeconds);
	}
	
	protected boolean checkCondition(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement) {
		if(this.conditionClass == null || conditionElement == null) {
			return true;
		}
		
		try {
			WebElement target;
			if(rootElement != null) {
				target = webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50))
						.until(conditionElement.elementToBeClickable(webDriver));				
			} else {
				target = webDriverWait.getWebDriverWaiter().withTimeout(Duration.ofSeconds(50))
						.until(conditionElement.elementToBeClickable(webDriver));
			}
			
			ActionCondition condition = conditionClass.getConstructor().newInstance();
			return condition.checkCondition(target);
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
			String message = "Error while instantiating Condition class";
			log.error(message);
			throw new RuntimeException(message, e);
		}
	}
	
	protected void positionBefore(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, WebElement element, 
			ExpectedCondition<WebElement> expectedCondition) {
		positionElement(positionBefore, webDriver, webDriverWait, element, expectedCondition);
	}
	
	protected void positionAfter(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, WebElement element, 
			ExpectedCondition<WebElement> expectedCondition) {
		positionElement(positionAfter, webDriver, webDriverWait, element, expectedCondition);
	}
	
	protected void positionElement(Position position, SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, WebElement element,
			ExpectedCondition<WebElement> expectedCondition) {
		if(position != null) {
			if(position.getY() != null) {		
				int y = element.getRect().getY()-position.getY();
				webDriver.getWebDriver().executeScript("window.scrollBy(0,"+y+");");
			}
			if(position.getX() != null) {
				while(element.getLocation().getX() > position.getX()) {
					webDriver.getWebDriver().findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.ARROW_LEFT));
				}	    	
			}			
		}
	}
	
	public static String getActionTag() {
		return Action.TAG;
	}
	
	public static Action createAction(SeleniumHubProperties seleniumHubProperties, Map<String, StepElement> mappedElements,
			Map<String, ?> mappedSubSteps, NndlNode mappedAction) {
		ActionType actionType = indentifyAction(mappedAction);
		Action createdAction;
		try {
			if(actionType.isSubstepsRequired()) {
				createdAction = actionType.getAction()
						.getConstructor(SeleniumHubProperties.class, NndlNode.class, Map.class, Map.class)
						.newInstance(seleniumHubProperties, mappedAction, mappedSubSteps, mappedElements);				
			} else {
				createdAction = actionType.getAction().getConstructor(SeleniumHubProperties.class,
						NndlNode.class, Map.class)
						.newInstance(seleniumHubProperties, mappedAction, mappedElements);
			}
				
			createdAction.setOrder(ActionExtractor.getOrder(mappedAction));
			createdAction.setRequirementStatus(ActionExtractor.getRequirementStatus(mappedAction));
			createdAction.setConditionClass(ActionExtractor.getConditionClass(mappedAction));
			createdAction.setConditionElement(ActionExtractor.getConditionElement(mappedAction, mappedElements));
			createdAction.setLimitRequirement(ActionExtractor.getLimitRequirement(mappedAction));
			createdAction.setPositionAfter(ActionExtractor.getPositionAfter(mappedAction));
			createdAction.setPositionBefore(ActionExtractor.getPositionBefore(mappedAction));
			createdAction.setOnEach(ActionExtractor.getOnEach(mappedAction));
			
			return createdAction;
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
			throw new RuntimeException("It wasn't possible to instantiate action: "+
					actionType.name(), e);
		}

	}
	
	public Advice run(SeleniumSndlWebDriver webDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws NndlActionException {		
		if(!isLoopCountAccepted(rootElement)) {
			return new ContinueAdvice();
		}
		
		Advice advice = null;
		
		try {
			if(rootElement != null && !isIgnoreRoot()) {
				advice = runNested(webDriver, webDriverWait, rootElement);
			} else {
				advice = runAction(webDriver, webDriverWait);
			}			
		} catch(ElementNotInteractableException e) {
			String msg = String.format("Action element not interactable. Action: %s", this);
			log.error(msg);
			throw new RuntimeException(msg, e);
		}
		return advice;
	}

	private boolean isLoopCountAccepted(IterationContent iterationContent) {
		if(iterationContent == null) {
			return true;
		}
		return iterationContent.getCount() % onEach == 0;
	}

	private static ActionType indentifyAction(NndlNode mappedAction) {
		log.debug("identifyAction. mappedAction: {}", mappedAction);
		
		return Arrays.stream(ActionType.values())
			.filter(e -> mappedAction.hasValueFromChild(e.getActionTag()))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Non identify action: "+mappedAction));
	}
}

package dev.edumelo.com.nndl_core.action;

import java.util.Arrays;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatus;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;
import lombok.Data;

@Data
//@Slf4j
public abstract class Action {
	private static final String TAG = "actions";		
	private int order;
	private int waitDuration;
	private RequirementStatus requirementStatus;
	private Class<ActionCondition> conditionClass;
	private boolean limitRequirement;
	private boolean actionPerformed;
	private Position positionBefore;
	private Position positionAfter;
	private int onEach;
	
	public abstract String getTag();
	public abstract boolean isIgnoreRoot();
	public abstract void runPreviousModification(ActionModificator modificiator);
	public abstract Advice runNested(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) throws ActionException;
	public abstract Advice runAction(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException;
	
	protected boolean checkCondition(WebElement element) {
		if(this.conditionClass == null) {
			return true;
		}
		
		try {
			ActionCondition condition = conditionClass.getConstructor().newInstance();
			return condition.checkCondition(element);
		} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
			String message = "Error while instantiating Condition class";
			//XXX Retornar
//			log.error(message);
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
	
	public static Action createAction(Map<String, StepElement> mappedElements, Map<String, ?> mappedSubSteps, Map<String, ?> mappedAction) {
		ActionType actionType = indentifyAction(mappedAction);
		Action createdAction = null;
		
		switch (actionType) {
			case ELEMENT_CLICK:
				createdAction = new Click(mappedAction, mappedElements);
				break;
			case HOVER:
				createdAction = new Hover(mappedAction, mappedElements);
				break;
			case FILL_INPUT:
				createdAction = new FillInput(mappedAction, mappedElements);
				break;
			case GOTO:
				createdAction = new Goto(mappedAction, mappedElements);
				break;
			case STORE_COOKIES:
				createdAction = new StoreCookies(mappedAction, mappedElements);
				break;
			case LOAD_COOKIES:
				createdAction = new LoadCookies(mappedAction, mappedElements);
				break;
			case SEND_KEY:
				createdAction = new SendKey(mappedAction, mappedElements);
				break;
			case LOOP:
				createdAction = new Loop(mappedAction, mappedSubSteps, mappedElements);
				break;
			case FILL:
				createdAction = new Fill(mappedAction, mappedElements);
				break;
			default:
				break;
		}
		
		createdAction.setOrder(ActionExtractor.getOrder(mappedAction));
		createdAction.setRequirementStatus(ActionExtractor.getRequirementStatus(mappedAction));
		createdAction.setConditionClass(ActionExtractor.getConditionClass(mappedAction));
		createdAction.setLimitRequirement(ActionExtractor.getLimitRequirement(mappedAction));
		createdAction.setPositionAfter(ActionExtractor.getPositionAfter(mappedAction));
		createdAction.setPositionBefore(ActionExtractor.getPositionBefore(mappedAction));
		createdAction.setOnEach(ActionExtractor.getOnEach(mappedAction));
		
		return createdAction;
	}
	
	
	public Advice run(SeleniumSndlWebDriver webDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) throws ActionException {
		//XXX Retornar
//		log.debug("run: rootElement: {}", rootElement);
		
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
			//XXX Retornar
//			log.error(msg);
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

	private static ActionType indentifyAction(Map<String, ?> mappedAction) {
		//XXX Retornar
//		log.debug("identifyAction. mappedAction: {}", mappedAction);
		
		return Arrays.stream(ActionType.values())
			.filter(e -> mappedAction.containsKey(e.getActionTag()))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Non identify action: "+mappedAction));
	}
}

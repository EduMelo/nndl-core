package dev.edumelo.com.nndl_core.action.impl.loop;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.Action;
import dev.edumelo.com.nndl_core.action.ActionModificator;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkAchievementStrategy;
import dev.edumelo.com.nndl_core.action.landmark.LandmarkStrategies;
import dev.edumelo.com.nndl_core.exceptions.checked.InfiniteScrollMaxLoopCountReached;
import dev.edumelo.com.nndl_core.exceptions.checked.NndlActionException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.scroll.InfiniteScroll;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollCondition;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollFactory;
import dev.edumelo.com.nndl_core.scroll.ListScroll;
import dev.edumelo.com.nndl_core.scroll.ListScrollFactory;
import dev.edumelo.com.nndl_core.scroll.ScrollObserver;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumHubProperties;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Loop extends Action {
	private static final Logger log = LoggerFactory.getLogger(Loop.class);
	private static final int DEFAULT_MAX_LOOP_COUNT = 50;
	
	private Class<ScrollObserver> infinitScrollObserverClass;
	private Class<InfiniteScrollCondition> conditionClass;
	private Step iterationStep;
	private LoopIterationScope iterationScope;
	private int scrollCount = 15;
	private Long limit;
	private boolean autoScrool;
	private boolean ignoreMaxLoopCountException;
	private Boolean throwTimeout;
    private Integer elementTimeoutWait;
    private NndlNode mappedAction;
	private NndlNode relevantNode;
	
	public Loop(SeleniumHubProperties seleniumHubProperties, NndlNode mappedAction,
			Map<String, ?> mappedSubSteps, Map<String, StepElement> mappedElements) {
		super(seleniumHubProperties, mappedAction, mappedElements);
		this.infinitScrollObserverClass = LoopExtractor.extractInfiniteScrollObserversClass(mappedAction);
		this.conditionClass = LoopExtractor.extractConditionClass(mappedAction);
		this.iterationStep = LoopExtractor.extractIterationStep(mappedAction, mappedSubSteps);
		this.iterationScope = LoopExtractor.extractIterationScope(mappedAction, mappedElements);
		this.scrollCount = LoopExtractor.extractScrollCount(mappedAction);
		this.autoScrool = LoopExtractor.extractAutoScroll(mappedAction);
		this.limit = LoopExtractor.extractLimit(mappedAction);
		this.ignoreMaxLoopCountException = LoopExtractor.extractIgnoreMaxLoopCountException(mappedAction);
		this.throwTimeout = LoopExtractor.extratctThrowTimeout(mappedAction);
		this.elementTimeoutWait = LoopExtractor.extractElementTimeoutWait(mappedAction);
		this.mappedAction = mappedAction;
		this.relevantNode = mappedAction;
	}
	
	@Override
	public String getTag() {
		return LoopExtractor.getTag();
	}
	
	@Override
	public boolean isIgnoreRoot() {
		return true;
	}
	
	@Override
	public NndlNode getRelevantNode() {
		return this.relevantNode;
	}
	
	@Override
	public LandmarkStrategies getDefaultWaitCondition() {
		return new LandmarkStrategies(LandmarkAchievementStrategy.NONE);
	}
	
	@Override
	public StepElement getRelevantElment() {
		return null;
	}
	
	@Override
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws NndlActionException {
		log.debug("runNested");
		return runElement(remoteWebDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws NndlActionException {
		log.debug("runAction");
		return runElement(remoteWebDriver, webDriverWait, null);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) 
			throws InfiniteScrollMaxLoopCountReached {
		int maxLoopCount = DEFAULT_MAX_LOOP_COUNT;
		
		switch (iterationScope.getType()) {
		case PAGE_ELEMENT:
			runInfiniteScroll(remoteWebDriver, webDriverWait, rootElement, maxLoopCount);			
			break;
		default:
			runListScroll(remoteWebDriver, webDriverWait, rootElement, maxLoopCount);
			break;
		}
        
		setActionPerformed(true);
		return new ContinueAdvice();
	}

	private void runListScroll(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement, int maxLoopCount) throws InfiniteScrollMaxLoopCountReached {
		ListScroll listScroll = ListScrollFactory.create();
		listScroll.scroll(maxLoopCount, limit);
	}

	private void runInfiniteScroll(SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement,
			int maxLoopCount) throws InfiniteScrollMaxLoopCountReached {
		InfiniteScroll infiniteScroll = InfiniteScrollFactory.create(conditionClass,
				remoteWebDriver, webDriverWait, rootElement, scrollCount, autoScrool,
				iterationScope, infinitScrollObserverClass, iterationStep, throwTimeout,
				elementTimeoutWait, mappedAction);
		try {
			infiniteScroll.scroll(maxLoopCount, limit);			
		} catch(InfiniteScrollMaxLoopCountReached e) {		
			log.error("Max loop count reached");
			if(!ignoreMaxLoopCountException) {
				throw e;
			}
		}
	}

	@Override
	public void runPreviousModification(ActionModificator modificiator) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this,
				org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

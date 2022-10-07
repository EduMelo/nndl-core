package dev.edumelo.com.nndl_core.action;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.contextAdapter.ExtractDataBindAdapter;
import dev.edumelo.com.nndl_core.scroll.InfiniteScroll;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollCondition;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollFactory;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollMaxLoopCountReached;
import dev.edumelo.com.nndl_core.scroll.ListScroll;
import dev.edumelo.com.nndl_core.scroll.ListScrollFactory;
import dev.edumelo.com.nndl_core.scroll.ScrollObserver;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.step.advice.ContinueAdvice;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class Loop extends Action {
	
	private static final Logger log = LoggerFactory.getLogger(Loop.class);
	
	private static final int DEFAULT_MAX_LOOP_COUNT = 50;
	
	private Class<ScrollObserver> infinitScrollObserverClass;
	private Class<ExtractDataBindAdapter> resultFillerClass;
	private Class<InfiniteScrollCondition> conditionClass;
	private ScrollObserver extractorObserver;
	private Collection<ExtractDataBind> extractData;
	private Step iterationStep;
	private LoopIterationScope iterationScope;
	private int scrollCount = 15;
	private Long limit;
	private boolean autoScrool;
	private boolean ignoreMaxLoopCountException;
	private Boolean throwTimeout;
    private Integer elementTimeoutWait;
	
	public Loop(Map<String, ?> mappedAction, Map<String, ?> mappedSubSteps, Map<String, StepElement> mappedElements) {
		this.infinitScrollObserverClass = LoopExtractor.extractInfiniteScrollObserversClass(mappedAction);
		this.resultFillerClass = LoopExtractor.extractResultFillerClass(mappedAction);
		this.conditionClass = LoopExtractor.extractConditionClass(mappedAction);
		this.iterationStep = LoopExtractor.extractIterationStep(mappedAction, mappedSubSteps);
		this.iterationScope = LoopExtractor.extractIterationScope(mappedAction, mappedElements);
		this.scrollCount = LoopExtractor.extractScrollCount(mappedAction);
		this.autoScrool = LoopExtractor.extractAutoScroll(mappedAction);
		this.limit = LoopExtractor.extractLimit(mappedAction);
		this.ignoreMaxLoopCountException = LoopExtractor.extractIgnoreMaxLoopCountException(mappedAction);
		this.throwTimeout = LoopExtractor.extratctThrowTimeout(mappedAction);
		this.elementTimeoutWait = LoopExtractor.extractElementTimeoutWait(mappedAction);
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
	public Advice runNested(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement)
					throws ActionException {
		log.debug("runNested");
		return runElement(sessionId, remoteWebDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		log.debug("runAction");
		return runElement(sessionId, remoteWebDriver, webDriverWait, null);
	}
	
	public Advice runElement(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) 
			throws InfiniteScrollMaxLoopCountReached {
		int maxLoopCount = DEFAULT_MAX_LOOP_COUNT;
		
		switch (iterationScope.getType()) {
		case PAGE_ELEMENT:
			runInfiniteScroll(sessionId, remoteWebDriver, webDriverWait, rootElement, maxLoopCount);			
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

	private void runInfiniteScroll(String sessionId, SeleniumSndlWebDriver remoteWebDriver,
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement,
			int maxLoopCount) throws InfiniteScrollMaxLoopCountReached {
		InfiniteScroll infiniteScroll = InfiniteScrollFactory.create(sessionId, conditionClass,
				remoteWebDriver, webDriverWait, rootElement, scrollCount, autoScrool,
				iterationScope, infinitScrollObserverClass, iterationStep, throwTimeout,
				elementTimeoutWait);
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
		return "Loop [infinitScrollObserverClass=" + infinitScrollObserverClass + ", resultFillerClass="
				+ resultFillerClass + ", conditionClass=" + conditionClass + ", extractorObserver=" + extractorObserver
				+ ", extractData=" + extractData + ", iterationStep=" + iterationStep + ", iterationScope="
				+ iterationScope + ", scrollCount=" + scrollCount + ", limit=" + limit + ", autoScrool=" + autoScrool
				+ ", ignoreMaxLoopCountException=" + ignoreMaxLoopCountException + "]";
	}
}

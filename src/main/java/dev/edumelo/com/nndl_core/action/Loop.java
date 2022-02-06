package dev.edumelo.com.nndl_core.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import dev.edumelo.com.nndl_core.DataBindExtractor;
import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.ExtractDataBindCreator;
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

public class Loop extends Action implements DataBindExtractor {
	private static final int DEFAULT_MAX_LOOP_COUNT = 50;
	
	private Class<ScrollObserver> infinitScrollObserverClass;
	private Class<ExtractDataBindCreator> resultFillerClass;
	private Class<InfiniteScrollCondition> conditionClass;
	private ScrollObserver extractorObserver;
	private Collection<ExtractDataBind> extractData;
	private Step iterationStep;
	private LoopIterationScope iterationScope;
	private int scrollCount = 15;
	private Long limit;
	private boolean autoScrool;
	private boolean ignoreMaxLoopCountException;
	
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
	}

	@Override
	public Collection<ExtractDataBind> getExtractDataBind() {
		return extractData;
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
	public Advice runNested(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) throws ActionException {
		//XXX Retornar
//		log.debug("runNested");
		return runElement(remoteWebDriver, webDriverWait, rootElement);
	}
	
	@Override
	public Advice runAction(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait) throws ActionException {
		//XXX Retornar
//		log.debug("runAction");
		return runElement(remoteWebDriver, webDriverWait, null);
	}
	
	public Advice runElement(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement) 
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void runInfiniteScroll(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait,
			IterationContent rootElement, int maxLoopCount) throws InfiniteScrollMaxLoopCountReached {
		extractData = new HashSet();
		InfiniteScroll infiniteScroll = InfiniteScrollFactory.create(conditionClass, remoteWebDriver, webDriverWait, rootElement, scrollCount, autoScrool, 
				extractData, iterationScope, infinitScrollObserverClass, iterationStep);
		try {
			infiniteScroll.scroll(maxLoopCount, limit);			
		} catch(InfiniteScrollMaxLoopCountReached e) {		
			//XXX Retornar
//			log.error("Max loop count reached");
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

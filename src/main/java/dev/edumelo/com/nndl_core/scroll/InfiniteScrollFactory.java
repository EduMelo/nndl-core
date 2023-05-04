package dev.edumelo.com.nndl_core.scroll;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.impl.loop.LoopIterationScope;
import dev.edumelo.com.nndl_core.action.impl.loop.StepElementIterationScope;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class InfiniteScrollFactory {

	private static final Logger log = LoggerFactory.getLogger(InfiniteScrollFactory.class);
	
	public static InfiniteScroll create(String sessionId,
			Class<InfiniteScrollCondition> conditionClass, SeleniumSndlWebDriver remoteWebDriver, 
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement,
			int scrollCount, boolean autoScrool, LoopIterationScope iterationScope, 
			Class<ScrollObserver> infinitScrollObserverClass, Step iterationStep,
			Boolean throwTimeout, Integer elementTimeoutWait) {
		InfiniteScrollCondition scrollCondition = createInfiniteScrollCondition(conditionClass);
		ScrollObserver extractorObserver = createExtractorObserver(sessionId, remoteWebDriver,
				webDriverWait, rootElement, scrollCondition, iterationScope,
				infinitScrollObserverClass, iterationStep, throwTimeout, elementTimeoutWait);
		InfiniteScroll infiniteScroll = new InfiniteScroll(remoteWebDriver, scrollCount, autoScrool,
				List.of(extractorObserver));
		return infiniteScroll;
	}
	
	private static InfiniteScrollCondition createInfiniteScrollCondition(Class<InfiniteScrollCondition> conditionClass) {
		if(conditionClass != null) {
			try {
				return conditionClass.getConstructor().newInstance();
			} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
				String message = "Error while instantiating Storer class";
				log.error(message);
				throw new RuntimeException(message, e);
			}
		}
		return new DefaultInfiniteScrollCondition();
	}
	
	private static ScrollObserver createExtractorObserver(String sessionId,
			SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, 
			IterationContent rootElement, InfiniteScrollCondition scrollCondition,
			LoopIterationScope iterationScope, Class<ScrollObserver> infinitScrollObserverClass,
			Step iterationStep, Boolean throwTimeout, Integer elementTimeoutWait) {
		StepElementIterationScope stepElementIterationScope = 
				(StepElementIterationScope) iterationScope;
		
		InfiniteScrollAdapter infiniteScrollAdapter = new InfiniteScrollAdapter(sessionId,
				remoteWebDriver, webDriverWait, iterationStep,
				new StepRunner(sessionId, remoteWebDriver, webDriverWait), scrollCondition,
				rootElement, stepElementIterationScope.getStepElement(), throwTimeout,
				elementTimeoutWait);

		if(infinitScrollObserverClass != null) {
			try {
				return infinitScrollObserverClass
						.getConstructor(InfiniteScrollAdapter.class)
						.newInstance(infiniteScrollAdapter);				
			} catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
				String message = "Error while instantiating Loop classes";
				log.error(message);
				throw new RuntimeException(message, e);
			}
		} else {
			return new DefaultScrollObserver(infiniteScrollAdapter);			
		}
	}

}

package dev.edumelo.com.nndl_core.scroll;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.action.LoopIterationScope;
import dev.edumelo.com.nndl_core.action.StepElementIterationScope;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepRunner;
import dev.edumelo.com.nndl_core.webdriver.IterationContent;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriverWaiter;

public class InfiniteScrollFactory {

	private static final Logger log = LoggerFactory.getLogger(InfiniteScrollFactory.class);
	
	public static InfiniteScroll create(Class<InfiniteScrollCondition> conditionClass, SeleniumSndlWebDriver remoteWebDriver, 
			SeleniumSndlWebDriverWaiter webDriverWait, IterationContent rootElement, int scrollCount, boolean autoScrool, Collection<ExtractDataBind> extractData, 
			LoopIterationScope iterationScope, Class<ScrollObserver> infinitScrollObserverClass, Step iterationStep) {
		InfiniteScrollCondition scrollCondition = createInfiniteScrollCondition(conditionClass);
		ScrollObserver extractorObserver = createExtractorObserver(remoteWebDriver, webDriverWait, rootElement, scrollCondition, extractData, 
				iterationScope, infinitScrollObserverClass, iterationStep);
		InfiniteScroll infiniteScroll = new InfiniteScroll(remoteWebDriver, scrollCount, autoScrool, List.of(extractorObserver));
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
	
	private static ScrollObserver createExtractorObserver(SeleniumSndlWebDriver remoteWebDriver, SeleniumSndlWebDriverWaiter webDriverWait, 
			IterationContent rootElement, InfiniteScrollCondition scrollCondition, Collection<ExtractDataBind> extractData, LoopIterationScope iterationScope, 
			Class<ScrollObserver> infinitScrollObserverClass, Step iterationStep) {		
		StepElementIterationScope stepElementIterationScope = (StepElementIterationScope) iterationScope;
		
		InfiniteScrollAdapter infiniteScrollAdapter = new InfiniteScrollAdapter(remoteWebDriver, webDriverWait, iterationStep, 
				new StepRunner(remoteWebDriver, webDriverWait, extractData), scrollCondition, extractData, rootElement, stepElementIterationScope.getStepElement());

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

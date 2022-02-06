package dev.edumelo.com.nndl_core.action;

import java.util.Map;

import dev.edumelo.com.nndl_core.ExtractDataBindCreator;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollCondition;
import dev.edumelo.com.nndl_core.scroll.ScrollObserver;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;

@SuppressWarnings("unchecked")
public class LoopExtractor {
	public static final String TAG = "loop";
	private static final String AUTO_SCROLL_TAG = "autoScroll";
	private static final String SCROLL_COUNT_TAG = "scrollCount";
	private static final String FILL_TAG = "fill";
	private static final String EXECUTE_TAG = "execute";
	private static final String ITERATION_SCOPE_TAG = "iterationScope";
	private static final String EXECUTE_CONDITIONS = "executeConditions";
	private static final String LIMIT_TAG = "limit";
	private static final String IGNORE_MAX_LOOP_COUNT_EXCEPTION = "ignoreMaxLoopCountException";
	private static final int DEFAULT_SCROLL_DEGREE = 15;
	
	public static String getTag() {
		return TAG;
	}

	public static boolean extractAutoScroll(Map<String, ?> mappedAction) {
		Object autoScrollValue = mappedAction.get(AUTO_SCROLL_TAG);
		if(autoScrollValue != null) {
			return (Boolean) autoScrollValue;
		}
		return true;
	}
	
	public static Long extractLimit(Map<String, ?> mappedAction) {
		Object value = mappedAction.get(LIMIT_TAG);
		if(value == null) {
			return null;
		}
		return ((Integer) value).longValue();
	}

	public static int extractScrollCount(Map<String, ?> mappedAction) {
		Object value = mappedAction.get(SCROLL_COUNT_TAG);
		if(value == null) {
			return DEFAULT_SCROLL_DEGREE;
		}
		return (Integer) value;
	}
	
	public static Class<ScrollObserver> extractInfiniteScrollObserversClass(Map<String, ?> mappedAction) {
		String className = (String) mappedAction.get(TAG);
		if(className == null) {
			return null;
		}
		try {
			return (Class<ScrollObserver>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			String message = String.format("Cannot found class by the name: %s", className);
			//XXX Retornar
//			log.error(message);
			throw new RuntimeException(message);
		}
	}

	public static LoopIterationScope extractIterationScope(Map<String, ?> mappedAction, Map<String, StepElement> mappedElements) {
		Map<String, ?> iterationScope = (Map<String, ?>) mappedAction.get(ITERATION_SCOPE_TAG);
		return LoopIterationScopeFactory.create(iterationScope, mappedElements);
	}

	public static Step extractIterationStep(Map<String, ?> mappedAction, Map<String, ?> mappedSubSteps) {
		if(mappedSubSteps == null) {
			return null;
		}
		
		String iterationStepName = (String) mappedAction.get(EXECUTE_TAG);
		return (Step) mappedSubSteps.get(iterationStepName);
	}

	public static Class<InfiniteScrollCondition> extractConditionClass(Map<String, ?> mappedAction) {
		String className = (String) mappedAction.get(EXECUTE_CONDITIONS);
		if(className != null) {
			try {
				return (Class<InfiniteScrollCondition>) Class.forName(className);			
			} catch (ClassNotFoundException e) {
				String message = String.format("Cannot found class by name: %s", className);
				//XXX Retornar
//				log.error(message);
				throw new RuntimeException(message, e);
			}			
		}
		return null;
	}
	
	public static Class<ExtractDataBindCreator> extractResultFillerClass(Map<String, ?> mappedAction) {
		String className = (String) mappedAction.get(FILL_TAG);
		if(className != null) {
			try {
				return (Class<ExtractDataBindCreator>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				String message = String.format("Cannot found class by the name: %s", className);
				//XXX Retornar
//				log.error(message);
				throw new RuntimeException(message, e);
			}			
		}
		
		return null;
	}

	public static boolean extractIgnoreMaxLoopCountException(Map<String, ?> mappedAction) {
		Object ignoreMaxLoopCountExceptionValue = mappedAction.get(IGNORE_MAX_LOOP_COUNT_EXCEPTION);
		if(ignoreMaxLoopCountExceptionValue == null) {
			return false;
		}
		return (boolean) ignoreMaxLoopCountExceptionValue;
	}
}

package dev.edumelo.com.nndl_core.action.impl.loop;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.contextAdapter.ExtractDataBindAdapter;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollCondition;
import dev.edumelo.com.nndl_core.scroll.ScrollObserver;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;

@SuppressWarnings("unchecked")
public class LoopExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(LoopExtractor.class);
	
	public static final String TAG = "loop";
	private static final String AUTO_SCROLL_TAG = "autoScroll";
	private static final String SCROLL_COUNT_TAG = "scrollCount";
	private static final String FILL_TAG = "fill";
	private static final String EXECUTE_TAG = "execute";
	private static final String ITERATION_SCOPE_TAG = "iterationScope";
	private static final String EXECUTE_CONDITIONS = "executeConditions";
	private static final String LIMIT_TAG = "limit";
	private static final String IGNORE_MAX_LOOP_COUNT_EXCEPTION = "ignoreMaxLoopCountException";
	private static final String THROW_TIMEOUT = "throwTimeout";
	private static final String TIMEOUT_WAIT = "timeoutWait";
	private static final int DEFAULT_SCROLL_DEGREE = 15;
	private static final int DEFAULT_TIMEOUT_WAIT = 50;
	
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
			log.error(message);
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
				log.error(message);
				throw new RuntimeException(message, e);
			}			
		}
		return null;
	}
	
	public static Class<ExtractDataBindAdapter<?>> extractResultFillerClass(Map<String, ?> mappedAction) {
		String className = (String) mappedAction.get(FILL_TAG);
		if(className != null) {
			try {
				return (Class<ExtractDataBindAdapter<?>>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				String message = String.format("Cannot found class by the name: %s", className);
				log.error(message);
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

	public static Boolean extratctThrowTimeout(Map<String, ?> mappedAction) {
		Object throwTimeout = mappedAction.get(THROW_TIMEOUT);
		if(throwTimeout == null) {
			return null;
		}
		return (Boolean) throwTimeout;
	}

	public static Integer extractElementTimeoutWait(Map<String, ?> mappedAction) {
		Object elementTimeoutWait = mappedAction.get(TIMEOUT_WAIT);
		if(elementTimeoutWait == null) {
			return DEFAULT_TIMEOUT_WAIT;
		}
		return (Integer) elementTimeoutWait;
	}
}

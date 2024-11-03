package dev.edumelo.com.nndl_core.action.impl.loop;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import dev.edumelo.com.nndl_core.contextAdapter.ExtractDataBindAdapter;
import dev.edumelo.com.nndl_core.exceptions.NndlParserException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.scroll.InfiniteScrollCondition;
import dev.edumelo.com.nndl_core.scroll.ScrollObserver;
import dev.edumelo.com.nndl_core.step.Step;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.utils.ClassUtils;

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
	private static final String THROW_TIMEOUT = "throwTimeout";
	private static final String TIMEOUT_WAIT = "timeoutWait";
	private static final int DEFAULT_SCROLL_DEGREE = 15;
	private static final int DEFAULT_TIMEOUT_WAIT = 50;
	
	public static String getTag() {
		return TAG;
	}

	public static boolean extractAutoScroll(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(AUTO_SCROLL_TAG, Boolean.class).orElse(true);
	}
	
	public static Long extractLimit(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(LIMIT_TAG, Long.class).orElse(null);
	}

	public static int extractScrollCount(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(SCROLL_COUNT_TAG, Integer.class).orElse(DEFAULT_SCROLL_DEGREE);
	}
	
	public static Class<ScrollObserver> extractInfiniteScrollObserversClass(NndlNode mappedAction) {
		return mappedAction
				.getScalarValueFromChild(TAG)
				.filter(StringUtils::isNotEmpty)
				.flatMap(ClassUtils::loadClass)
				.map(c -> (Class<ScrollObserver>) c)
				.orElse(null);
	}

	public static LoopIterationScope extractIterationScope(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		NndlNode iterationScope = mappedAction.getValueFromChild(ITERATION_SCOPE_TAG).orElseThrow(NndlParserException
				.get("Action Loop should have "+ITERATION_SCOPE_TAG+" tag.", mappedAction));
		return LoopIterationScopeFactory.create(iterationScope, mappedElements);
	}

	public static Step extractIterationStep(NndlNode mappedAction, Map<String, ?> mappedSubSteps) {
		if(mappedSubSteps == null) {
			return null;
		}
		
		String iterationStepName = mappedAction.getScalarValueFromChild(EXECUTE_TAG)
				.orElseThrow(NndlParserException.get("Action Loop should have "+EXECUTE_TAG+" tag.", mappedAction));
		return (Step) mappedSubSteps.get(iterationStepName);
	}

	public static Class<InfiniteScrollCondition> extractConditionClass(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(EXECUTE_CONDITIONS)
				.filter(StringUtils::isNotEmpty)
				.flatMap(ClassUtils::loadClass)
				.map(c -> (Class<InfiniteScrollCondition>) c)
				.orElse(null);
	}
	
	public static Class<ExtractDataBindAdapter<?>> extractResultFillerClass(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(FILL_TAG)
				.filter(StringUtils::isNotEmpty)
				.flatMap(ClassUtils::loadClass)
				.map(c -> (Class<ExtractDataBindAdapter<?>>) c)
				.orElse(null);
	}

	public static boolean extractIgnoreMaxLoopCountException(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(IGNORE_MAX_LOOP_COUNT_EXCEPTION, Boolean.class).orElse(false);
	}

	public static Boolean extratctThrowTimeout(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(THROW_TIMEOUT, Boolean.class).orElse(null);
	}

	public static Integer extractElementTimeoutWait(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild(TIMEOUT_WAIT, Integer.class).orElse(DEFAULT_TIMEOUT_WAIT);
	}
}

package dev.edumelo.com.nndl_core.action;

import java.util.Map;

import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatus;
import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatusFactory;
import dev.edumelo.com.nndl_core.action.utils.Position;
import dev.edumelo.com.nndl_core.exceptions.NndlParserRuntimeException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;
import dev.edumelo.com.nndl_core.step.StepElement;
import dev.edumelo.com.nndl_core.utils.ClassUtils;

@SuppressWarnings("unchecked")
public class ActionExtractor {

	private static final String POSITION_AFTER = "positionAfter";
	private static final String POSITION_BEFORE = "positionBefore";
	private static final String LIMIT_REQUIREMENT_TAG = "limitRequirement";
	private static final String CONDITION_TAG = "condition";
	private static final String ON_EACH_TAG = "onEach";
	private static final String CONDITION_CLASS_TAG = "class";
	
	public static int getOrder(NndlNode mappedAction) {
		return mappedAction.getScalarValueFromChild("order", Integer.class)
				.orElseThrow(() -> new NndlParserRuntimeException("It should be defined the order",
						mappedAction));
	}
	
	public static RequirementStatus getRequirementStatus(NndlNode mappedAction) {
		return RequirementStatusFactory.createRequirementStatus(mappedAction);
	}

	public static int getOnEach(NndlNode mappedAction) {
		return mappedAction
				.getScalarValueFromChild(ON_EACH_TAG, Integer.class)
				.orElse(1);
	}
	
	public static Position getPositionAfter(NndlNode mappedAction) {
		return mappedAction
				.getValueFromChild(POSITION_AFTER)
				.map(node -> new Position(node))
				.orElse(null);
	}
	
	public static Position getPositionBefore(NndlNode mappedAction) {
		return mappedAction
				.getValueFromChild(POSITION_BEFORE)
				.map(node -> new Position(node))
				.orElse(null);
	}
	
	public static boolean getLimitRequirement(NndlNode mappedAction) {
		return mappedAction
				.getScalarValueFromChild(LIMIT_REQUIREMENT_TAG, Boolean.class)
				.orElse(false);
	}
	
	public static Class<ActionCondition> getConditionClass(NndlNode mappedAction) {
		return mappedAction
				.getValueFromChild(CONDITION_TAG)
				.flatMap(node -> node.getScalarValueFromChild(CONDITION_CLASS_TAG))
				.flatMap(className -> ClassUtils.loadClass(className))
				.map(clazz -> (Class<ActionCondition>) clazz)
				.orElse(null);
	}

	public static StepElement getConditionElement(NndlNode mappedAction, Map<String, StepElement> mappedElements) {
		return mappedAction
				.getValueFromChild(CONDITION_TAG)
				.flatMap(node -> node.getScalarValueFromChild(CONDITION_CLASS_TAG))
				.map(mappedElements::get)
				.orElse(null);
	}
	
	
}

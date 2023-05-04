package dev.edumelo.com.nndl_core.action;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatus;
import dev.edumelo.com.nndl_core.action.requirementStatus.RequirementStatusFactory;
import dev.edumelo.com.nndl_core.action.utils.Position;
import dev.edumelo.com.nndl_core.step.StepElement;

@SuppressWarnings("unchecked")
public class ActionExtractor {

	private static final Logger log = LoggerFactory.getLogger(ActionExtractor.class);
	
	private static final String POSITION_AFTER = "positionAfter";
	private static final String POSITION_BEFORE = "positionBefore";
	private static final String LIMIT_REQUIREMENT_TAG = "limitRequirement";
	private static final String CONDITION_TAG = "condition";
	private static final String ON_EACH_TAG = "onEach";
	private static final String CONDITION_CLASS_TAG = "class";
	private static final String CONDITION_ELEMENT_TAG = "element";
	
	public static int getOrder(Map<String, ?> mappedAction) {
		return (int) mappedAction.get("order");
	}
	
	public static RequirementStatus getRequirementStatus(Map<String, ?> mappedAction) {
		return RequirementStatusFactory.createRequirementStatus(mappedAction);
	}

	public static int getOnEach(Map<String, ?> mappedAction) {
		Object onEachValue = mappedAction.get(ON_EACH_TAG);
		if(onEachValue != null) {
			return (Integer) onEachValue;
		}
		return 1;
	}
	
	public static Position getPositionAfter(Map<String, ?> mappedAction) {
		Object positionAfter = mappedAction.get(POSITION_AFTER);
		if(positionAfter != null) {
			return new Position((Map<String, ?>) positionAfter);			
		}
		return null;
	}
	
	public static Position getPositionBefore(Map<String, ?> mappedAction) {
		Object positionBefore = mappedAction.get(POSITION_BEFORE);
		if(positionBefore != null) {
			return new Position((Map<String, ?>) positionBefore);			
		}
		return null;
	}
	
	public static boolean getLimitRequirement(Map<String, ?> mappedAction) {
		Object limitRequirementValue = mappedAction.get(LIMIT_REQUIREMENT_TAG);
		if(limitRequirementValue instanceof Boolean) {
			return (Boolean) limitRequirementValue;
		}
		return false;
	}
	
	public static Class<ActionCondition> getConditionClass(Map<String, ?> mappedAction) {
		Object conditionValue = mappedAction.get(CONDITION_TAG);
		if(Objects.nonNull(conditionValue)) {
			Map<String, Object> mapaCondition = (Map<String, Object>) conditionValue; 
			String className = (String) mapaCondition.get(CONDITION_CLASS_TAG);
			try {
				return (Class<ActionCondition>) Class.forName(className);			
			} catch (ClassNotFoundException e) {
				String message = String.format("Cannot found class by name: %s", className);
				log.error(message);
				throw new RuntimeException(message, e);
			}
		}
		return null;
	}

	public static StepElement getConditionElement(Map<String, ?> mappedAction, 
			Map<String, StepElement> mappedElements) {
		Object conditionValue = mappedAction.get(CONDITION_TAG);
		if(Objects.nonNull(conditionValue)) {
			Map<String, Object> mapaCondition = (Map<String, Object>) conditionValue; 
			String elementKey = (String) mapaCondition.get(CONDITION_ELEMENT_TAG);
			return mappedElements.get(elementKey);
		}
		return null;
	}
	
	
}

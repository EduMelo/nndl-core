package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Map;

public class RequirementStatusFactory {
	private static final String TAG = "requirementStatus";
	private static final String TYPE_TAG = "type";

	public static RequirementStatus createRequirementStatus(Map<String, ?> actions) {
		RequirementStatusType type = identifyRequirementStatus(actions);
		if(type == null) {
			return new RequiredRequirementStatus();
		}
		switch(type) {
			case NON_REQUIRED:
				return new NonRequiredRequimentStatus(actions);
			case STEP_BREAKER:
				return new StepBreakerRequirementStatus(actions);
			case RESTART_STEP:
				return new RestartStepRequirementStatus(actions);
			case REQUIRED:
			default:
				return new RequiredRequirementStatus(actions);
		}
	}

	private static RequirementStatusType identifyRequirementStatus(Map<String, ?> actions) {
		Object requirementStatusObject = actions.get(TAG);
		
		if(requirementStatusObject != null) {
			Map<String, ?> mappedRequirement = (Map<String, ?>) requirementStatusObject;
			Object typeObject = mappedRequirement.get(TYPE_TAG);
			if(typeObject != null) {
				return RequirementStatusType.getType((String) typeObject);				
			}
		}
		return null;
	}

}

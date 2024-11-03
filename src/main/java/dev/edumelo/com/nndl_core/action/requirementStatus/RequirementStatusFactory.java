package dev.edumelo.com.nndl_core.action.requirementStatus;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class RequirementStatusFactory {
	private static final String TAG = "requirementStatus";
	private static final String TYPE_TAG = "type";

	public static RequirementStatus createRequirementStatus(NndlNode actions) {
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

	private static RequirementStatusType identifyRequirementStatus(NndlNode actions) {
		return actions
				.getValueFromChild(TAG)
				.flatMap(n -> n.getScalarValueFromChild(TYPE_TAG))
				.map(RequirementStatusType::getType)
				.orElse(null);
	}

}

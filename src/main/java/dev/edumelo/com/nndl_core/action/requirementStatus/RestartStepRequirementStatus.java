package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Map;

import dev.edumelo.com.nndl_core.step.RunBreakerActionNotPerformed;
import dev.edumelo.com.nndl_core.step.StepBreakerActionNotPerformed;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RestartStepRequirementStatus extends RequirementStatus {
	private static final String RESTART_COUNT_TAG = "restartCount";
	private static final String FALLBACK_TAG = "fallback";
	private int restartCount;
	private Exception fallBack;

	public RestartStepRequirementStatus(Map<String, ?> actions) {
		super(actions);
		restartCount = getRestatCount(actions);
		fallBack = getFallBackException(actions);
	}

	private Exception getFallBackException(Map<String, ?> actions) {
		Object typeObject = actions.get(FALLBACK_TAG);
		
		String msg = String.format("Action not performed.");
		if(typeObject != null) {
			RequirementStatusType type = RequirementStatusType.getType((String) typeObject);
			switch(type) {
			case NON_REQUIRED:
				return null;
			case REQUIRED:
				return new RunBreakerActionNotPerformed(msg);
			case STEP_BREAKER:
				return new StepBreakerActionNotPerformed(msg);
			default:
				throw new RequirementStatusNonExistent();
		}
		}
		return null;
	}

	private int getRestatCount(Map<String, ?> actions) {
		Object restartCount = actions.get(RESTART_COUNT_TAG);
		if(restartCount != null) {
			return (Integer) restartCount;
		}
		return 0;
	}

}

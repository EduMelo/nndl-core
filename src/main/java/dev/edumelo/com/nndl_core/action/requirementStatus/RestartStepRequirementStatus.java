package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Map;
import java.util.Objects;

import dev.edumelo.com.nndl_core.step.RunBreakerActionNotPerformed;
import dev.edumelo.com.nndl_core.step.StepBreakerActionNotPerformed;

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

	public int getRestartCount() {
		return restartCount;
	}
	public void setRestartCount(int restartCount) {
		this.restartCount = restartCount;
	}
	public Exception getFallBack() {
		return fallBack;
	}
	public void setFallBack(Exception fallBack) {
		this.fallBack = fallBack;
	}
	public static String getRestartCountTag() {
		return RESTART_COUNT_TAG;
	}
	public static String getFallbackTag() {
		return FALLBACK_TAG;
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

	@Override
	public int hashCode() {
		return Objects.hash(fallBack, restartCount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestartStepRequirementStatus other = (RestartStepRequirementStatus) obj;
		return Objects.equals(fallBack, other.fallBack) && restartCount == other.restartCount;
	}

}

package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Objects;

import dev.edumelo.com.nndl_core.exceptions.checked.NndlFlowBreakerException;
import dev.edumelo.com.nndl_core.exceptions.checked.StepBreakerActionNotPerformed;
import dev.edumelo.com.nndl_core.exceptions.unchecked.RunBreakerActionNotPerformed;
import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class RestartStepRequirementStatus extends RequirementStatus {
	private static final String RESTART_COUNT_TAG = "restartCount";
	private static final String FALLBACK_TAG = "fallback";
	private int restartCount;
	private Exception fallBack;

	public RestartStepRequirementStatus(NndlNode actions) {
		super(actions);
		restartCount = actions.getScalarValueFromChild(RESTART_COUNT_TAG, Integer.class).orElse(0);
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
	public void setFallBack(NndlFlowBreakerException fallBack) {
		this.fallBack = fallBack;
	}
	public static String getRestartCountTag() {
		return RESTART_COUNT_TAG;
	}
	public static String getFallbackTag() {
		return FALLBACK_TAG;
	}

	private Exception getFallBackException(NndlNode action) {
		String msg = String.format("Action not performed.");
		return action.getScalarValueFromChild(FALLBACK_TAG)
				.map(RequirementStatusType::getType)
				.map(type -> {
					switch (type) {
						case NON_REQUIRED:
							return null;
						case REQUIRED:
							return new RunBreakerActionNotPerformed(msg, action);
						case STEP_BREAKER:
							return new StepBreakerActionNotPerformed(msg, action);
						default:
							throw new RequirementStatusNonExistent();
					}
				})
				.orElse(null);
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

package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequirementStatus {
	public static final String TAG = "requirementStatus";
	private static final String TYPE_TAG = "type";
	private static final String RETRIES_TAG = "retries";
	private static final String STEP_TREATMENT_TAG = "stepTreatment";
	private RequirementStatusType type;
	private int retries;
	private String stepTreatment;

	public RequirementStatus(RequirementStatusType type) {
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	public RequirementStatus(Map<String, ?> mappedAction) {
		Map<String, ?> mappedRequirements = (Map<String, ?>) mappedAction.get(TAG);
		type = extractType(mappedRequirements);
		retries = extractRetries(mappedRequirements);
		stepTreatment = extractStepTreatment(mappedRequirements);
	}

	private String extractStepTreatment(Map<String, ?> mappedRequirements) {
		Object stepTreatmentValue = mappedRequirements.get(STEP_TREATMENT_TAG);
		if(stepTreatmentValue != null) {
			return (String) stepTreatmentValue;
		}
		return null;
	}

	private int extractRetries(Map<String, ?> mappedAction) {
		Object retries = mappedAction.get(RETRIES_TAG);
		if(retries != null) {
			return (Integer) retries;
		}
		return 0;
	}

	private RequirementStatusType extractType(Map<String, ?> mappedAction) {
		Object requiredValue = mappedAction.get(TYPE_TAG);
		if(Objects.nonNull(requiredValue)) {
			String requiredStatus = (String) requiredValue;
			return RequirementStatusType.getType(requiredStatus);
			
		}
		return RequirementStatusType.REQUIRED;
	}
}

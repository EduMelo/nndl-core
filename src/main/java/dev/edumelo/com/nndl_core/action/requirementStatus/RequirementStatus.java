package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Map;
import java.util.Objects;

public class RequirementStatus {
	public static final String TAG = "requirementStatus";
	private static final String TYPE_TAG = "type";
	private static final String RETRIES_TAG = "retries";
	private static final String STEP_TREATMENT_TAG = "stepTreatment";
	private RequirementStatusType type;
	private int retries;
	private String stepTreatment;
	
	public RequirementStatus(RequirementStatusType type, int retries, String stepTreatment) {
		this.type = type;
		this.retries = retries;
		this.stepTreatment = stepTreatment;
	}
	
	public RequirementStatusType getType() {
		return type;
	}
	public void setType(RequirementStatusType type) {
		this.type = type;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public String getStepTreatment() {
		return stepTreatment;
	}
	public void setStepTreatment(String stepTreatment) {
		this.stepTreatment = stepTreatment;
	}
	public static String getTag() {
		return TAG;
	}
	public static String getTypeTag() {
		return TYPE_TAG;
	}
	public static String getRetriesTag() {
		return RETRIES_TAG;
	}
	public static String getStepTreatmentTag() {
		return STEP_TREATMENT_TAG;
	}
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

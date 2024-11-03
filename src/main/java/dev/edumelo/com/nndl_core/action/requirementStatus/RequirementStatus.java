package dev.edumelo.com.nndl_core.action.requirementStatus;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

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
	
	public RequirementStatus(NndlNode mappedAction) {
		type = mappedAction.getValueFromChild(TAG)
				.flatMap(n -> n.getScalarValueFromChild(TYPE_TAG))
				.map(RequirementStatusType::getType)
				.orElse(RequirementStatusType.REQUIRED);
		stepTreatment = mappedAction.getValueFromChild(TAG)
				.flatMap(n -> n.getScalarValueFromChild(STEP_TREATMENT_TAG))
				.orElse(null);
		retries = mappedAction.getScalarValueFromChild(RETRIES_TAG, Integer.class).orElse(0);
	}

}

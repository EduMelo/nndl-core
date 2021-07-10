package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Map;

public class RequiredRequirementStatus extends RequirementStatus {

	public RequiredRequirementStatus(Map<String, ?> mappedAction) {
		super(mappedAction);
	}

	public RequiredRequirementStatus() {
		super(RequirementStatusType.REQUIRED);
	}

}

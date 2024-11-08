package dev.edumelo.com.nndl_core.action.requirementStatus;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class RequiredRequirementStatus extends RequirementStatus {

	public RequiredRequirementStatus(NndlNode mappedAction) {
		super(mappedAction);
	}

	public RequiredRequirementStatus() {
		super(RequirementStatusType.REQUIRED);
	}

}

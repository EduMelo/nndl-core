package dev.edumelo.com.nndl_core.action.requirementStatus;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum RequirementStatusType {
	REQUIRED("required"), 
	NON_REQUIRED("nonRequired"),
	STEP_BREAKER("stepBreaker"),
	RESTART_STEP("restartStep");
	
	private String tag;
	
	private RequirementStatusType(String tag) {
		this.tag = tag;
	}
	
	public static RequirementStatusType getType(String tag) {
		return Arrays.stream(RequirementStatusType.values())
			.filter(e -> e.getTag().equals(tag))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Non identify requirement status: "+tag));
	}
}

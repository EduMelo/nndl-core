package dev.edumelo.com.nndl_core.action;

import java.util.Arrays;

import lombok.Getter;

public enum LoopIterationScopeType {
	PAGE_ELEMENT("pageElement"),
	LIST("list");

	@Getter
	private String tag;
	
	LoopIterationScopeType(String tag) {
		this.tag = tag;
	}

	static LoopIterationScopeType byTag(String typeValue) {
		return Arrays.stream(LoopIterationScopeType.values())
		.filter(p -> p.getTag().equals(typeValue))
		.findFirst()
		.orElseThrow();
	}

}

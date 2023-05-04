package dev.edumelo.com.nndl_core.action.impl.loop;

import java.util.Arrays;

public enum LoopIterationScopeType {
	PAGE_ELEMENT("pageElement"),
	LIST("list");

	private String tag;
	
	public String getTag() {
		return tag;
	}

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

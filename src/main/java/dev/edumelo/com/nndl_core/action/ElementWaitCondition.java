package dev.edumelo.com.nndl_core.action;

import java.util.Arrays;

public enum ElementWaitCondition {
	NONE("none"), PRESENT("present"), VISIBLE("visible"), CLICKABLE("clickable");
	
	private String tag;
	
	private ElementWaitCondition(String tag) { 
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public static ElementWaitCondition getFromTag(String tag) {
		return Arrays.stream(ElementWaitCondition.values())
			.filter(e -> e.getTag().equals(tag))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Tag not found for ElementWaitCondition. Tag: "+tag));
	}
}

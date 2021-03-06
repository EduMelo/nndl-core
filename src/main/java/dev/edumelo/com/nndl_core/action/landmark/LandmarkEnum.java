package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;

public enum LandmarkEnum {
	ELEMENT("element"), COOLDOWN("cooldown");
	
	private String tag;
	
	LandmarkEnum(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public static LandmarkEnum getLandMarkEnum(String tag) {
		return Arrays.asList(LandmarkEnum.values()).stream()
				.filter(l -> l.getTag().equals(tag))
				.findFirst()
				.orElseThrow();
	}
}

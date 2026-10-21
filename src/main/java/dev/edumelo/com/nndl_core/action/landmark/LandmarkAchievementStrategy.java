package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.function.Function;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;


public enum LandmarkAchievementStrategy {
	NONE("none", e -> null),
	IS_DISPLAYED("displayed", NndlElement::isDisplayed), 
	IS_ENABLED("enabled", NndlElement::isEnabled),
	IS_PRESENT("present", e -> e != null),
	IS_VISIBLE("visible", e -> e.isDisplayed() && e.isEnabled()),
	IS_CLICKABLE("clickable", e -> e.isDisplayed() && e.isEnabled());
	
	private String tag;
	private Function<NndlElement, Boolean> function;
	
	private LandmarkAchievementStrategy(String tag, Function<NndlElement, Boolean> function) {
		this.tag = tag;
		this.function = function;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public Function<NndlElement, Boolean> getFunction() {
		return function;
	}
	
	public static LandmarkAchievementStrategy getFromTag(String tag) {
		return Arrays.stream(LandmarkAchievementStrategy.values())
			.filter(e -> e.getTag().equals(tag))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Tag not found for ElementWaitCondition. Tag: "+tag));
	}
}

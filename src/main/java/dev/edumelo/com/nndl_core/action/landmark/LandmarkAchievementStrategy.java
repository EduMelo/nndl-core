package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.function.Function;

import org.openqa.selenium.WebElement;

public enum LandmarkAchievementStrategy {
	NONE("none", e -> null),
	IS_DISPLAYED("displayed", WebElement::isDisplayed), 
	IS_ENABLED("enabled", WebElement::isEnabled),
	IS_PRESENT("present", e -> e != null),
	IS_VISIBLE("visible", e -> e.isDisplayed() && e.isEnabled()),
	IS_CLICKABLE("clickable", e -> e.isDisplayed() && e.isEnabled());
	
	private String tag;
	private Function<WebElement, Boolean> function;
	
	private LandmarkAchievementStrategy(String tag, Function<WebElement, Boolean> function) {
		this.tag = tag;
		this.function = function;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public Function<WebElement, Boolean> getFunction() {
		return function;
	}
	
	public static LandmarkAchievementStrategy getFromTag(String tag) {
		return Arrays.stream(LandmarkAchievementStrategy.values())
			.filter(e -> e.getTag().equals(tag))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Tag not found for ElementWaitCondition. Tag: "+tag));
	}
}

package dev.edumelo.com.nndl_core.step;

import java.util.Map;
import java.util.Objects;

import org.openqa.selenium.By;

import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import lombok.Data;

@Data
public class StepElement {
	private static final String IGNORE_ROOT_TAG = "ignoreRoot";
	private static final String MATCH_EXP_TAG = "matchExp";
	private static final String NAME_TAG = "name";
	private static final String TAG = "elements";
	
	private String name;
	private String matchExp;
	private boolean ignoreRoot; 
	
	public static String getTag() {
		return StepElement.TAG;
	}
	
	public StepElement(Map<String, ?> mappedElement) {
		this.name = (String) mappedElement.get(NAME_TAG);
		this.matchExp = (String) mappedElement.get(MATCH_EXP_TAG);
		this.ignoreRoot = getIgnoreRoot(mappedElement);
	}

	private boolean getIgnoreRoot(Map<String, ?> mappedElement) {
		Object ignorRootValue = mappedElement.get(IGNORE_ROOT_TAG);
		if(Objects.isNull(ignorRootValue)) {
			return false;
		}
		return (boolean) ignorRootValue;
	}
	
	public StepElement(StepElement stepElement) {
		setName(stepElement.getName());
		setMatchExp(stepElement.getMatchExp());
	}
	
	public By getLocator(NndlWebDriver remoteWebDriver) {
		return By.cssSelector(this.matchExp);
	}

}

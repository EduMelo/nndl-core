package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class LandmarkStrategies {
	public final static String WAIT_CONDITION_TAG = "waitCondition";
	
	private List<LandmarkAchievementStrategy> strategies;

	public LandmarkStrategies(LandmarkAchievementStrategy... strategies) {
		this.strategies = Arrays.asList(strategies);
	}

	public List<LandmarkAchievementStrategy> getStrategies() {
		return strategies;
	}

	public Stream<LandmarkAchievementStrategy> stream() {
		return strategies.stream();
	}
	
	public ExpectedCondition<WebElement> getConditionIfAchieved(SearchContext shadowRoot, String matchExp) {
		return driver -> {
			List<WebElement> elements = shadowRoot.findElements(By.cssSelector(matchExp));
			if (elements.isEmpty()) {
		        return null;
		    }
			WebElement element = elements.get(0);
			return strategies.stream().map(LandmarkAchievementStrategy::getFunction)
					.allMatch(function -> function.apply(element)) ? element : null;
		};
	}
	
	public ExpectedCondition<List<WebElement>> getConditionIfAllAchieved(SearchContext shadowRoot, String matchExp) {
		return driver -> {
			List<WebElement> elements = shadowRoot.findElements(By.cssSelector(matchExp));
			if (elements.isEmpty()) {
		        return null;
		    }
			
			return strategies.stream()
			.map(LandmarkAchievementStrategy::getFunction)
			.allMatch(function -> elements.stream().allMatch(e -> function.apply(e))) ? elements : null;
		};
	}
	
}

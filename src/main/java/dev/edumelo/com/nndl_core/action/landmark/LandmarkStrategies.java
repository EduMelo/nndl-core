package dev.edumelo.com.nndl_core.action.landmark;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;
import dev.edumelo.com.nndl_core.webdriver.NndlElementCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlElementsCondition;
import dev.edumelo.com.nndl_core.webdriver.NndlLocator;
import dev.edumelo.com.nndl_core.webdriver.NndlSearchContext;


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
	
	public NndlElementCondition getConditionIfAchieved(NndlSearchContext shadowRoot, String matchExp) {
		return () -> {
			List<NndlElement> elements = shadowRoot.findAll(NndlLocator.css(matchExp));
			if (elements.isEmpty()) {
		        return null;
		    }
			NndlElement element = elements.get(0);
			return strategies.stream().map(LandmarkAchievementStrategy::getFunction)
					.allMatch(function -> function.apply(element)) ? element : null;
		};
	}
	
	public NndlElementsCondition getConditionIfAllAchieved(NndlSearchContext shadowRoot, String matchExp) {
		return () -> {
			List<NndlElement> elements = shadowRoot.findAll(NndlLocator.css(matchExp));
			if (elements.isEmpty()) {
		        return null;
		    }
			
			return strategies.stream()
			.map(LandmarkAchievementStrategy::getFunction)
			.allMatch(function -> elements.stream().allMatch(e -> function.apply(e))) ? elements : null;
		};
	}
	
}

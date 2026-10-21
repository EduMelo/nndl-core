package dev.edumelo.com.nndl_core.action.landmark;


import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlElementCondition;

public interface Landmark {
	Advice getLandMarkAdvice();
	Integer getTimeout();
	LandmarkStrategies getStrategy();
	NndlElementCondition landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies);
}

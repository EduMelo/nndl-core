package dev.edumelo.com.nndl_core.action.landmark;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;

public interface Landmark {
	Advice getLandMarkAdvice();
	Integer getTimeout();
	LandmarkStrategies getStrategy();
	ExpectedCondition<WebElement> landmarkAchiveable(NndlWebDriver remoteWebDriver,
			LandmarkStrategies strategies);
}

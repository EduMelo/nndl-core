package dev.edumelo.com.nndl_core.action.landmark;

import org.openqa.selenium.By;

import dev.edumelo.com.nndl_core.step.advice.Advice;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;

public interface Landmark {
	By getLocator(NndlWebDriver remoteWebDriver);
	Advice getLandMarkAdvice();
	Integer getTimeout();
}

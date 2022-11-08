package dev.edumelo.com.nndl_core.scroll;

import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public interface ScrollObserver {	
	void setIterationScope(String iterationScope);
	int execute(SeleniumSndlWebDriver webDriver, int loopCount) throws ScrollContinueException, ScrollStopException;
}

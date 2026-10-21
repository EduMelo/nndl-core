package dev.edumelo.com.nndl_core.scroll;

import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriver;

public interface ScrollObserver {	
	void setIterationScope(String iterationScope);
	int execute(SeleniumNndlWebDriver webDriver, int loopCount) throws ScrollContinueException, ScrollStopException;
}

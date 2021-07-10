package dev.edumelo.com.nndl_core.webdriver;

import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.RequiredArgsConstructor;

//@Service
@RequiredArgsConstructor
public class SeleniumSndlWebDriverWaiter implements NndlWebDriverWaiter {

	private WebDriverWait webDriverWait;
	private final BrowserControllerDriverConfiguration browserCOnfiguration;
	private final SeleniumSndlWebDriver webDriver;
	
	@Override
	@SuppressWarnings("unchecked")
	public WebDriverWait getWebDriverWaiter() {
		return webDriverWait;
	}
	
	public void refreshWaiter() {
		this.webDriverWait = browserCOnfiguration.createWait(webDriver.getWebDriver());
	}

}

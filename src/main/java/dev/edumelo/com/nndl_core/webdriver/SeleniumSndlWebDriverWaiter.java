package dev.edumelo.com.nndl_core.webdriver;

import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumSndlWebDriverWaiter implements NndlWebDriverWaiter {

	private WebDriverWait webDriverWait;
	private final BrowserControllerDriverConfiguration browserCOnfiguration;
	private final SeleniumSndlWebDriver webDriver;
	
	public SeleniumSndlWebDriverWaiter(BrowserControllerDriverConfiguration browserCOnfiguration,
			SeleniumSndlWebDriver webDriver) {
		this.browserCOnfiguration = browserCOnfiguration;
		this.webDriver = webDriver;
	}

	public SeleniumSndlWebDriverWaiter(SeleniumSndlWebDriver webDriver) {
		this.webDriver = webDriver;
		this.browserCOnfiguration = webDriver.getBrowserCOnfiguration();
	}

	@Override
	@SuppressWarnings("unchecked")
	public WebDriverWait getWebDriverWaiter() {
		return webDriverWait;
	}
	
	public void refreshWaiter() {
		this.webDriverWait = browserCOnfiguration.createWait(webDriver.getWebDriver());
	}

}

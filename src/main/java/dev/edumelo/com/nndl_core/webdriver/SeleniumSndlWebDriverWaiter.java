package dev.edumelo.com.nndl_core.webdriver;

import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumSndlWebDriverWaiter implements NndlWebDriverWaiter {

	private WebDriverWait webDriverWait;
	private final BrowserControllerDriverConfiguration browserConfiguration;
	private final SeleniumSndlWebDriver webDriver;
	
	public SeleniumSndlWebDriverWaiter(BrowserControllerDriverConfiguration browserConfiguration,
			SeleniumSndlWebDriver webDriver) {
		this.browserConfiguration = browserConfiguration;
		this.webDriver = webDriver;
	}

	public SeleniumSndlWebDriverWaiter(SeleniumSndlWebDriver webDriver) {
		this.webDriver = webDriver;
		this.browserConfiguration = webDriver.getBrowserCOnfiguration();
	}

	@Override
	@SuppressWarnings("unchecked")
	public WebDriverWait getWebDriverWaiter() {
		return webDriverWait;
	}
	
	public void refreshWaiter() {
		this.webDriverWait = browserConfiguration.createWait(webDriver.getWebDriver());
	}

}

package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;

import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumSndlWebDriver implements NndlWebDriver {
	
	private RemoteWebDriver webDriver;
	private final BrowserControllerDriverConfiguration browserCOnfiguration;

	public SeleniumSndlWebDriver(BrowserControllerDriverConfiguration browserCOnfiguration) {
		this.browserCOnfiguration = browserCOnfiguration;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RemoteWebDriver getWebDriver() {
		return webDriver;
	}
	
	public void refreshWebDriver() {
		try {
			this.webDriver = browserCOnfiguration.createRemoteDriver();
		} catch (MalformedURLException e) {
			String msg = "Mal formed URL while creating web driver";
			//XXX Retornar
//			log.error(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	public void quitWebDriver() {
		this.webDriver.quit();
	}

}

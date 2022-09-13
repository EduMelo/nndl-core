package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumSndlWebDriver implements NndlWebDriver {
	
	private static final Logger log = LoggerFactory.getLogger(SeleniumSndlWebDriver.class);
	
	private RemoteWebDriver webDriver;
	private final BrowserControllerDriverConfiguration browserCOnfiguration;

	public SeleniumSndlWebDriver(BrowserControllerDriverConfiguration browserCOnfiguration) {
		this.browserCOnfiguration = browserCOnfiguration;
	}
	
	public BrowserControllerDriverConfiguration getBrowserCOnfiguration() {
		return browserCOnfiguration;
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
			log.error(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	public void quitWebDriver() {
		if(this.webDriver != null) {
			this.webDriver.quit();			
		}
	}

}

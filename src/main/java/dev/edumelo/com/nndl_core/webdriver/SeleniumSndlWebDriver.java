package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumSndlWebDriver implements NndlWebDriver {
	
	private static final Logger log = LoggerFactory.getLogger(SeleniumSndlWebDriver.class);
	
	private final BrowserControllerDriverConfiguration browserCOnfiguration;
	private RemoteWebDriver webDriver;
	private SessionId sessionId;

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
	
	public String getSessionId() {
		return sessionId.toString();
	}
	
	public void refreshWebDriver() {
		try {
			webDriver = browserCOnfiguration.createRemoteDriver();
			sessionId = webDriver.getSessionId();			
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

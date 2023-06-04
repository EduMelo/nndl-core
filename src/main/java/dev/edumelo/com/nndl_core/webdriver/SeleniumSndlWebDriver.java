package dev.edumelo.com.nndl_core.webdriver;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumSndlWebDriver implements NndlWebDriver {
	
	private static final Logger log = LoggerFactory.getLogger(SeleniumSndlWebDriver.class);
	
	private final BrowserControllerDriverConfiguration browserConfiguration;
	private RemoteWebDriver webDriver;
	private SessionId sessionId;
	private List<BrowserArgumentsContextAdapter> browserArgumentsContextAdapter;

	public SeleniumSndlWebDriver(BrowserControllerDriverConfiguration browserConfiguration,
			List<BrowserArgumentsContextAdapter> browserArgumentsContextAdapter) {
		this.browserConfiguration = browserConfiguration;
		this.browserArgumentsContextAdapter = browserArgumentsContextAdapter;
	}
	
	public BrowserControllerDriverConfiguration getBrowserCOnfiguration() {
		return browserConfiguration;
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
			webDriver = browserConfiguration.createRemoteDriver(browserArgumentsContextAdapter);
			sessionId = webDriver.getSessionId();			
		} catch (MalformedURLException e) {
			String msg = "Mal formed URL while creating web driver";
			log.error(msg);
			throw new RuntimeException(msg, e);
		}
	}
	
	public void quitWebDriver() {
		try {
			if(this.webDriver != null) {
				this.webDriver.quit();			
			}			
		} catch(WebDriverException e) {
			log.error("It wasn't possible to quit the webDriver with sessonId: "+sessionId);
		}
	}

}

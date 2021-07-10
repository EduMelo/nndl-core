package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;

import org.openqa.selenium.remote.RemoteWebDriver;

import lombok.RequiredArgsConstructor;

//@Service
//XXX Retornar
//@Slf4j
@RequiredArgsConstructor
public class SeleniumSndlWebDriver implements NndlWebDriver {
	
	private RemoteWebDriver webDriver;
	private final BrowserControllerDriverConfiguration browserCOnfiguration;

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

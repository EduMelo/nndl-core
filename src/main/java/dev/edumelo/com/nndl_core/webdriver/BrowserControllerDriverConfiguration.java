package dev.edumelo.com.nndl_core.webdriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Browser controller driver
 */
public class BrowserControllerDriverConfiguration {

	private Logger log = LoggerFactory.getLogger(BrowserControllerDriverConfiguration.class);
	private final SeleniumHubProperties properties;

	public BrowserControllerDriverConfiguration(SeleniumHubProperties properties) {
		this.properties = properties;
	}

    public WebDriverWait createWait(RemoteWebDriver remoteWebDriver) {
	    return new WebDriverWait(remoteWebDriver, 0);
    }

	public RemoteWebDriver createRemoteDriver() throws MalformedURLException {
		RemoteWebDriver driver = new RemoteWebDriver(properties.getRemoteDriveUrl(), configOptions());
		return driver;
	}

	private MutableCapabilities configOptions() {
		MutableCapabilities options;

		switch (properties.getBrowser()) {
		case FIREFOX:
			options = new FirefoxOptions();
			break;
		case INTERNETEXPLORER:
			options = new InternetExplorerOptions();
			break;
		case CHROME:
		default:
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments(
					"--start-maximized",
					"--blink-settings=imagesEnabled=false",
					"--disable-notifications"
			);
			
			try {
				URL extensionUrl = ClassLoader.getSystemResource("youtube_no_buffer_stop_auto_playing.crx");
				File extensionFile = new File(extensionUrl.toURI());
				chromeOptions.addExtensions(extensionFile);
			} catch (URISyntaxException e) {
				log.error("An error occurred when tried to load chrome extensions");
			}
			options = chromeOptions;
		}

		options.setCapability("plataform", properties.getPlataform().name());
		options.setCapability("version", properties.getBrowserVersion());

		return options;
	}

}

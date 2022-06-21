package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Browser controller driver
 */
public class BrowserControllerDriverConfiguration {
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
			chromeOptions.addArguments(properties.getBrowserArguments());
			chromeOptions.addExtensions(properties.getExtensionFile());
			options = chromeOptions;
		}

		options.setCapability("plataform", properties.getPlataform().name());
		options.setCapability("version", properties.getBrowserVersion());

		return options;
	}

}

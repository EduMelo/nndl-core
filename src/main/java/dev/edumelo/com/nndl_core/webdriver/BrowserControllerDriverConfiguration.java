package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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

    public SeleniumHubProperties getProperties() {
		return properties;
	}
    
    public void removeBrowserArguments(String argument) {
    	if(properties != null) {
    		properties.removeBrowserArguments(argument);
    	}
    }

	public WebDriverWait createWait(RemoteWebDriver remoteWebDriver) {
	    return new WebDriverWait(remoteWebDriver, Duration.ZERO);
    }

	public RemoteWebDriver createRemoteDriver(
			List<BrowserArgumentsContextAdapter> browserArgumentsContextAdapter)
					throws MalformedURLException {
		RemoteWebDriver driver = new RemoteWebDriver(properties.getRemoteDriveUrl(),
				configOptions(browserArgumentsContextAdapter));
		return driver;
	}

	private MutableCapabilities configOptions(
			List<BrowserArgumentsContextAdapter> browserArgumentsContextAdapter) {
		MutableCapabilities options;

		String[] browserArguments = properties.getBrowserArguments();
		for (BrowserArgumentsContextAdapter adapter : browserArgumentsContextAdapter) {
			browserArguments = Arrays.stream(browserArguments)
			.map(a -> adapter.adapt(a))
			.filter(Objects::nonNull)
			.collect(Collectors.toList())
			.toArray(new String[0]);
		}
		
		switch (properties.getBrowser()) {
		case FIREFOX:
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments(browserArguments);
			options = firefoxOptions;
			break;
		case INTERNETEXPLORER:
			options = new InternetExplorerOptions();
			break;
		case CHROME:
		default:
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments(browserArguments);
			chromeOptions.addExtensions(properties.getExtensionFiles());
			chromeOptions.setExperimentalOption("prefs", properties.getExperimentalOptionPrefs());
			
			options = chromeOptions;
		}
		
		if(properties.getPlataform() != null) {
			options.setCapability("platformName", properties.getPlataform().name());			
		}
		
		if(StringUtils.isNotEmpty(properties.getBrowserVersion())) {
			options.setCapability("browserVersion", properties.getBrowserVersion());			
		}

		return options;
	}

	@Override
	public String toString() {
		return "BrowserControllerDriverConfiguration [properties=" + properties + "]";
	}

}

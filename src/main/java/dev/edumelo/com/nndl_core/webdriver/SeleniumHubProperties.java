package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Selenium Hub properties
 */
//@Component
//@EnableConfigurationProperties
//@ConfigurationProperties(prefix="grfnkl.seleniumhub")
//XXX Retornar
//@Slf4j
@NoArgsConstructor
@Data
@ToString
public class SeleniumHubProperties {
	
	enum PlataformType {LINUX, ANDROID}
	enum BrowserType {CHROME, FIREFOX, INTERNETEXPLORER}
	private String environment;
	private String browser;
	private String browserVersion = "";
	private String plataform;
	private Boolean windowMaximize;
	private Long implicitWait;
	private String host;
	private Integer port;
	private String authUser;
	private String authPassword;
	
	/**
	 * @return The selenium hub plataform to be used
	 */
	public PlataformType getPlataform() {
		return Optional.ofNullable(plataform)
				.map(String::toUpperCase)
				.map(PlataformType::valueOf)
				.orElse(PlataformType.LINUX);
	}
	
	/**
	 * @return The selenium hub browser to be used
	 */
	public BrowserType getBrowser() {
		return Optional.ofNullable(browser)
				.map(String::toUpperCase)
				.map(BrowserType::valueOf)
				.orElse(BrowserType.CHROME);
	}

	/**
	 * @return The selenium hub url
	 * @throws MalformedURLException If the url is malformed
	 */
	public URL getRemoteDriveUrl() throws MalformedURLException {
		String url;
		if(authUser == null) {
			url = String.format("http:/%s:%d/wd/hub", host, port);
		} else {
			url = String.format("http://%s:%s@%s:%d/wd/hub", authUser, authPassword, host, port);
		}
		//XXX Retornar
//		log.debug(String.format("Connecting to remote server: %s", url));
		return new URL(url);
	}
	
}

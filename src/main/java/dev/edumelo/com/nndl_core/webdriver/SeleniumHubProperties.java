package dev.edumelo.com.nndl_core.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Selenium Hub properties
 */
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
	
	public SeleniumHubProperties() {
	}

	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getBrowserVersion() {
		return browserVersion;
	}
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	public Boolean getWindowMaximize() {
		return windowMaximize;
	}
	public void setWindowMaximize(Boolean windowMaximize) {
		this.windowMaximize = windowMaximize;
	}
	public Long getImplicitWait() {
		return implicitWait;
	}
	public void setImplicitWait(Long implicitWait) {
		this.implicitWait = implicitWait;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getAuthUser() {
		return authUser;
	}
	public void setAuthUser(String authUser) {
		this.authUser = authUser;
	}
	public String getAuthPassword() {
		return authPassword;
	}
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public void setPlataform(String plataform) {
		this.plataform = plataform;
	}

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

	@Override
	public String toString() {
		return "SeleniumHubProperties [environment=" + environment + ", browser=" + browser + ", browserVersion="
				+ browserVersion + ", plataform=" + plataform + ", windowMaximize=" + windowMaximize + ", implicitWait="
				+ implicitWait + ", host=" + host + ", port=" + port + ", authUser=" + authUser + ", authPassword="
				+ authPassword + "]";
	}
}

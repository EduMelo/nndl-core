package dev.edumelo.com.nndl_core.webdriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selenium Hub properties
 */
public class SeleniumHubProperties {
	
	private static final Logger log = LoggerFactory.getLogger(SeleniumHubProperties.class);
	private static final String DOWNLOAD_DIRECTORY = "/tmp/";
	
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
	private boolean ssl;
	private String[] browserArguments;
	private String[] extensions;
	private List<File> extensionFiles;
	private Map<String, Object> experimentalOptionsPrefs;
	
	public SeleniumHubProperties() {
		experimentalOptionsPrefs = new HashMap<>();
		experimentalOptionsPrefs.put("download.default_directory", DOWNLOAD_DIRECTORY);
	}
	
	public Object getDownloadDirectory() {
		return experimentalOptionsPrefs.get("download.default_directory");
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
	public boolean isSsl() {
		return ssl;
	}
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
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
	public String[] getBrowserArguments() {
		return browserArguments;
	}
	public void setBrowserArguments(String[] browserArguments) {
		this.browserArguments = browserArguments;
	}
	public void removeBrowserArguments(String argument) {
		List<String> list = new ArrayList<String>(Arrays.asList(this.browserArguments));
		list.remove(argument);
		this.browserArguments = list.toArray(new String[0]);
	}
	public String[] getExtensions() {
		return extensions;
	}
	public void setExtensions(String... extensions) {
		this.extensions = extensions;
	}
	public List<File> getExtensionFiles() {
		return extensionFiles;
	}
	public void addExtensionFile(File extensionFile) {
		if(extensionFiles == null) {
			extensionFiles = new ArrayList<>();
		}
		extensionFiles.add(extensionFile);
	}
	public Map<String, Object> getExperimentalOptionPrefs() {
		return experimentalOptionsPrefs;
	}
	public void addExperimentalOptionPrefs(String key, String value) {
		experimentalOptionsPrefs.put(key, value);
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
			url = String.format("%s://%s:%d", getProtocol(), host, port);
		} else {
			url = String.format("%s://%s:%s@%s:%d", getProtocol(), authUser, authPassword, host, port);
		}
		log.debug(String.format("Connecting to remote server: %s", url));
		return new URL(url);
	}

	private Object getProtocol() {
		if(isSsl()) {
			return "https";
		}
		return "http";
	}

	@Override
	public String toString() {
		return "SeleniumHubProperties [environment=" + environment + ", browser=" + browser + ", browserVersion="
				+ browserVersion + ", plataform=" + plataform + ", windowMaximize=" + windowMaximize + ", implicitWait="
				+ implicitWait + ", host=" + host + ", port=" + port + ", authUser=" + authUser + ", authPassword="
				+ authPassword + ", browserArguments=" + Arrays.toString(browserArguments) + ", extensionUrlSpec="
				+ Arrays.toString(extensions) + "]";
	}

}

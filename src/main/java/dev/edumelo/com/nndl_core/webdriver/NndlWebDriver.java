package dev.edumelo.com.nndl_core.webdriver;

import java.util.List;
import java.util.Set;

public interface NndlWebDriver extends NndlSearchContext {

	void open();

	void quit();

	String getSessionId();

	String getPageSource();

	void get(String url);

	String getCurrentUrl();

	void refresh();

	NndlElement activeElement();

	Object executeScript(String script, Object... args);

	List<String> getWindowHandles();

	void switchToWindow(String handle);

	void closeWindow();

	Set<NndlCookie> getCookies();

	void addCookie(NndlCookie cookie);

	void hover(NndlElement element);

}

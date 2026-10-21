package dev.edumelo.com.nndl_core.webdriver;

public interface NndlBrowserSession {

	SeleniumHubProperties properties();

	NndlWebDriver open();

	NndlWebDriverWaiter waiter(NndlWebDriver driver);

}

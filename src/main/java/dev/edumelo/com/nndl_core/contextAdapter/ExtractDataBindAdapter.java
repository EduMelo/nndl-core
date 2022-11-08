package dev.edumelo.com.nndl_core.contextAdapter;

import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public interface ExtractDataBindAdapter<T extends ExtractDataBind> extends ContextAdapter  {

	public ExtractDataBind createFromElement(SeleniumSndlWebDriver webDriver, WebElement element);
	
}

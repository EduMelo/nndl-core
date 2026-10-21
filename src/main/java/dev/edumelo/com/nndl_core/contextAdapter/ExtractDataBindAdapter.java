package dev.edumelo.com.nndl_core.contextAdapter;

import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.webdriver.SeleniumNndlWebDriver;

public interface ExtractDataBindAdapter<T extends ExtractDataBind> extends ContextAdapter  {

	public ExtractDataBind createFromElement(SeleniumNndlWebDriver webDriver, WebElement element);
	
}

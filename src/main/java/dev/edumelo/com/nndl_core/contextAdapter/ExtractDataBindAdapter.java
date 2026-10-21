package dev.edumelo.com.nndl_core.contextAdapter;


import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;

public interface ExtractDataBindAdapter<T extends ExtractDataBind> extends ContextAdapter  {

	public ExtractDataBind createFromElement(NndlWebDriver webDriver, NndlElement element);
	
}

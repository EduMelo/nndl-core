package dev.edumelo.com.nndl_core.contextAdapter;


import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.webdriver.NndlWebDriver;
import dev.edumelo.com.nndl_core.webdriver.NndlElement;

public class ClipboardTextFactory implements ExtractDataBindAdapter<ClipboardText>  {

	@Override
	public ExtractDataBind createFromElement(NndlWebDriver webDriver, NndlElement element) {
		String text = (String) webDriver.executeScript("navigator.clipboard.readText();");
		ClipboardText clipboardText = new ClipboardText();
		clipboardText.setText(text);
		return clipboardText;
	}

}

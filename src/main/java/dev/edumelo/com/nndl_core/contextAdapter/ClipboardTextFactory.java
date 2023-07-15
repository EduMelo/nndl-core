package dev.edumelo.com.nndl_core.contextAdapter;

import org.openqa.selenium.WebElement;

import dev.edumelo.com.nndl_core.ExtractDataBind;
import dev.edumelo.com.nndl_core.webdriver.SeleniumSndlWebDriver;

public class ClipboardTextFactory implements ExtractDataBindAdapter<ClipboardText>  {

	@Override
	public ExtractDataBind createFromElement(SeleniumSndlWebDriver webDriver, WebElement element) {
		String text = (String) webDriver.getWebDriver()
				.executeScript("navigator.clipboard.readText();");
		ClipboardText clipboardText = new ClipboardText();
		clipboardText.setText(text);
		return clipboardText;
	}

}

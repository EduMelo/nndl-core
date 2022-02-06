package dev.edumelo.com.nndl_core.webdriver;

import org.openqa.selenium.WebElement;

public class IterationContent {
	private WebElement rootElement;
	private int count;
	
	public IterationContent(WebElement rootElement, int count) {
		this.rootElement = rootElement;
		this.count = count;
	}

	public WebElement getRootElement() {
		return rootElement;
	}

	public int getCount() {
		return count;
	}
	
}

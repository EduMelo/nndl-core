package dev.edumelo.com.nndl_core.webdriver;

import org.openqa.selenium.WebElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IterationContent {
	private WebElement rootElement;
	private int count;
	
}

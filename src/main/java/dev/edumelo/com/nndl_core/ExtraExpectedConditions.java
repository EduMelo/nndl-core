package dev.edumelo.com.nndl_core;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ExtraExpectedConditions {
	
	/**
	   * An expectation for checking that there is at all child WebElement as a part of parent element to be visible.
	   *
	   * @param parent     used as parent element.
	   * @param childLocator used to find child element. For example td By.xpath("./tr/td")
	   * @return the list of WebElements once they are located
	   */
	  public static ExpectedCondition<List<WebElement>> presenceOfNestedElementsLocatedBy(final WebElement parent, final By childLocator) {
	    return new ExpectedCondition<List<WebElement>>() {
			@Override
			public List<WebElement> apply(WebDriver driver) {
				List<WebElement> allChildren = parent.findElements(childLocator);
				
				return allChildren.isEmpty() ? null : allChildren;
			}
		};
	  }

}

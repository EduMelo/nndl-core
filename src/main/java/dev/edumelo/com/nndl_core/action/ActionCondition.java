package dev.edumelo.com.nndl_core.action;

import org.openqa.selenium.WebElement;

public interface ActionCondition {

	boolean checkCondition(WebElement element);
	
}

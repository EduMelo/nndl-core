package dev.edumelo.com.nndl_core.scroll;

import org.openqa.selenium.WebElement;

public class DefaultInfiniteScrollCondition implements InfiniteScrollCondition {

	@Override
	public boolean checkCondition(WebElement element) {
		return true;
	}

}

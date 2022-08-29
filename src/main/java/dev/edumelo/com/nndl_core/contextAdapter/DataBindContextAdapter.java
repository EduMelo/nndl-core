package dev.edumelo.com.nndl_core.contextAdapter;

import org.openqa.selenium.WebElement;

public interface DataBindContextAdapter extends ContextAdapter {
	public void bindFromElement(WebElement element);
	
}

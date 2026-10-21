package dev.edumelo.com.nndl_core.action;

import dev.edumelo.com.nndl_core.webdriver.NndlElement;


public interface ActionCondition {

	boolean checkCondition(NndlElement element);
	
}

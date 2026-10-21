package dev.edumelo.com.nndl_core.scroll;

import dev.edumelo.com.nndl_core.webdriver.NndlElement;


public class DefaultInfiniteScrollCondition implements InfiniteScrollCondition {

	@Override
	public boolean checkCondition(NndlElement element) {
		return true;
	}

}

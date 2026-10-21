package dev.edumelo.com.nndl_core.webdriver;

import java.util.List;

public interface NndlSearchContext {

	NndlElement find(NndlLocator locator);

	List<NndlElement> findAll(NndlLocator locator);

}

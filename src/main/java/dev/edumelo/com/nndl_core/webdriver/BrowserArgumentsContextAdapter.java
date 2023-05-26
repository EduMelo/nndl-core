package dev.edumelo.com.nndl_core.webdriver;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;

@FunctionalInterface
public interface BrowserArgumentsContextAdapter extends ContextAdapter {

	public String adapt(String a);

}

package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Set;
import dev.edumelo.com.nndl_core.webdriver.NndlCookie;


public abstract class CookieStorerAdapter implements ContextAdapter {
	public abstract void storeCookies(Object[] params, Set<NndlCookie> cookies);
}

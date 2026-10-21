package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Set;
import dev.edumelo.com.nndl_core.webdriver.NndlCookie;


public abstract class CookieRetrieverAdapter implements ContextAdapter {
	public abstract Set<NndlCookie> getCookies(Object... params);
}

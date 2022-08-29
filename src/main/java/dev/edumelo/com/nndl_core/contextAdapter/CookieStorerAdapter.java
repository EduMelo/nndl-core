package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Set;

import org.openqa.selenium.Cookie;

public abstract class CookieStorerAdapter implements ContextAdapter {
	public abstract void storeCookies(Object[] params, Set<Cookie> cookies);
}

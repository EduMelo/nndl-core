package dev.edumelo.com.nndl_core.contextAdapter;

import java.util.Set;

import org.openqa.selenium.Cookie;

public abstract class CookieRetrieverAdapter implements ContextAdapter {
	public abstract Set<Cookie> getCookies(Object... params);
}

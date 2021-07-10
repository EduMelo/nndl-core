package dev.edumelo.com.nndl_core.action;

import java.util.Set;

import org.openqa.selenium.Cookie;

public interface LoadCookiesRetriever {
	Set<Cookie> getCookies(Object... params);
}

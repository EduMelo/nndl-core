package dev.edumelo.com.nndl_core.webdriver;

import java.util.Date;

public final class NndlCookie {

	private final String name;
	private final String value;
	private final String domain;
	private final String path;
	private final Date expiry;
	private final boolean secure;
	private final boolean httpOnly;
	private final String sameSite;

	public NndlCookie(String name, String value, String domain, String path, Date expiry,
			boolean secure, boolean httpOnly, String sameSite) {
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
		this.expiry = expiry;
		this.secure = secure;
		this.httpOnly = httpOnly;
		this.sameSite = sameSite;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDomain() {
		return domain;
	}

	public String getPath() {
		return path;
	}

	public Date getExpiry() {
		return expiry;
	}

	public boolean isSecure() {
		return secure;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public String getSameSite() {
		return sameSite;
	}

}

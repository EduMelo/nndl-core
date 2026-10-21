package dev.edumelo.com.nndl_core.webdriver;

public final class NndlLocator {

	public enum Kind {
		CSS, TAG
	}

	private final Kind kind;
	private final String value;

	private NndlLocator(Kind kind, String value) {
		this.kind = kind;
		this.value = value;
	}

	public static NndlLocator css(String selector) {
		return new NndlLocator(Kind.CSS, selector);
	}

	public static NndlLocator tag(String tagName) {
		return new NndlLocator(Kind.TAG, tagName);
	}

	public Kind getKind() {
		return kind;
	}

	public String getValue() {
		return value;
	}

}

package dev.edumelo.com.nndl_core.utils;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;

public class SpecialParamsTranslater {

	private static final String WEBDRIVER_SESSION_ID = "webDriverSessionId";

	public static Object translateIfSpecial(String nndlRunnerSessionId, String rawParams) {
		if(rawParams == null) {
			return null;
		}
		if(rawParams.startsWith("$")) {
			return translate(nndlRunnerSessionId, rawParams.substring(1));
		}
		return rawParams;
	}

	private static Object translate(String nndlRunnerSessionId, String rawParams) {
		if(rawParams.startsWith(WEBDRIVER_SESSION_ID)) {
			return ContextAdapterHandler.getWebDriverSessionId(nndlRunnerSessionId);
		}
		return null;
	}

}

package dev.edumelo.com.nndl_core.utils;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;

public class SpecialParamsTranslater {

	private static final String CONTEXT_ADAPTER_TAG = "contextAdapter";

	public static String translateIfSpecial(String sessionId, String rawParams) {
		if(rawParams == null) {
			return null;
		}
		if(rawParams.startsWith("$")) {
			return translate(sessionId, rawParams.substring(1));
		}
		return rawParams;
	}

	private static String translate(String sessionId, String rawParams) {
		if(rawParams.startsWith(CONTEXT_ADAPTER_TAG)) {
			return ContextAdapterHandler.getParam(sessionId, rawParams.split(".")[1]);
		}
		return null;
	}

}

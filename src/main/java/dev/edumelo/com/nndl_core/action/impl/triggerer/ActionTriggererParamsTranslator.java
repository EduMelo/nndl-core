package dev.edumelo.com.nndl_core.action.impl.triggerer;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapterHandler;

public class ActionTriggererParamsTranslator {

	public static Object[] translate(String nndlRunnerSessionId, String[] triggerParams) {
		if(triggerParams == null) {
			return null;
		}
		Object[] translatedTriggerParam = new Object[triggerParams.length];
		for (int i = 0; i < triggerParams.length; i++) {
			String param = triggerParams[i];
			String translateParam  = specialTranslation(nndlRunnerSessionId, param);
			if(translateParam == null) {
				translatedTriggerParam[i] = param; 
			} else {
				translatedTriggerParam[i] = translateParam;
			}
		}
		return translatedTriggerParam;
	}

	private static String specialTranslation(String nndlRunnerSessionId, String param) {
		switch (param) {
		case "$webDriverSessionId":
			return ContextAdapterHandler.getWebDriverSessionId(nndlRunnerSessionId);
		default:
			return null;
		}
	}

}

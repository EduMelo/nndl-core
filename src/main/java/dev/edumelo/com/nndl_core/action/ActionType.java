package dev.edumelo.com.nndl_core.action;

public enum ActionType {
	GOTO("gotoUrl"),
	STORE_COOKIES("storeCookies"),
	LOAD_COOKIES("loadCookies"),
	FILL_INPUT("fillInput"),
	CLEAR_INPUT("clearInput"),
	ELEMENT_CLICK("elementClick"),
	HOVER("hover"),
	SEND_KEY("sendKey"),
	LOOP("loop"),
	EXTRACT("extract"),
	ELEMENT_PAINT("elementPaint"),
	ELEMENT_MARK("elementMark"),
	ACTION_TRIGGERER("actionTriggerer");
	
	private String actionTag;
	
	private ActionType(String actionTag) {
		this.actionTag = actionTag;
	}
	
	public String getActionTag() {
		return this.actionTag;
	}
}

package dev.edumelo.com.nndl_core.action;

public enum ActionType {
	GOTO("gotoUrl"),
	STORE_COOKIES("storeCookies"),
	LOAD_COOKIES("loadCookies"),
	FILL_INPUT("fillInput"),
	ELEMENT_CLICK("elementClick"),
	HOVER("hover"),
	SEND_KEY("sendKey"),
	LOOP("loop"),
	FILL("fill"),
	ELEMENT_PAINT("elementPaint");
	
	private String actionTag;
	
	private ActionType(String actionTag) {
		this.actionTag = actionTag;
	}
	
	public String getActionTag() {
		return this.actionTag;
	}
}

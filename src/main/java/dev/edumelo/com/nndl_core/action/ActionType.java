package dev.edumelo.com.nndl_core.action;

import dev.edumelo.com.nndl_core.action.impl.ClearInput;
import dev.edumelo.com.nndl_core.action.impl.ElementClick;
import dev.edumelo.com.nndl_core.action.impl.ElementMark;
import dev.edumelo.com.nndl_core.action.impl.ElementPaint;
import dev.edumelo.com.nndl_core.action.impl.Extract;
import dev.edumelo.com.nndl_core.action.impl.FillInput;
import dev.edumelo.com.nndl_core.action.impl.Goto;
import dev.edumelo.com.nndl_core.action.impl.Hover;
import dev.edumelo.com.nndl_core.action.impl.LoadCookies;
import dev.edumelo.com.nndl_core.action.impl.SendKey;
import dev.edumelo.com.nndl_core.action.impl.StoreCookies;
import dev.edumelo.com.nndl_core.action.impl.loop.Loop;
import dev.edumelo.com.nndl_core.action.impl.triggerer.ActionTriggerer;

public enum ActionType {
	GOTO("gotoUrl", Goto.class, false),
	STORE_COOKIES("storeCookies", StoreCookies.class, false),
	LOAD_COOKIES("loadCookies", LoadCookies.class, false),
	FILL_INPUT("fillInput", FillInput.class, false),
	CLEAR_INPUT("clearInput", ClearInput.class, false),
	ELEMENT_CLICK("elementClick", ElementClick.class, false),
	HOVER("hover", Hover.class, false),
	SEND_KEY("sendKey", SendKey.class, false),
	LOOP("loop", Loop.class, true),
	EXTRACT("extract", Extract.class, false),
	ELEMENT_PAINT("elementPaint", ElementPaint.class, false),
	ELEMENT_MARK("elementMark", ElementMark.class, false),
	ACTION_TRIGGERER("actionTriggerer", ActionTriggerer.class, false);
	
	private String actionTag;
	private Class<? extends Action> action;
	private boolean substepsRequired;
	
	private <T extends Action> ActionType(String actionTag, Class<T> action,
			boolean substepsRequired) {
		this.actionTag = actionTag;
		this.action = action;
		this.substepsRequired = substepsRequired;
	}
	
	public String getActionTag() {
		return actionTag;
	}
	
	public Class<? extends Action> getAction() {
		return action;
	}
	
	public boolean isSubstepsRequired() {
		return substepsRequired;
	}
}

package dev.edumelo.com.nndl_core.action.impl.triggerer;

import dev.edumelo.com.nndl_core.contextAdapter.ContextAdapter;

public abstract class  ActionTrigger implements ContextAdapter {
	public abstract String getTriggerId();
	public abstract void triggerAction(Object[] params);
}

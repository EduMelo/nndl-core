package dev.edumelo.com.nndl_core.exceptions.unchecked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlFlowBreakerRuntimeException extends NndlRuntimeActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6332928198701423154L;

	public NndlFlowBreakerRuntimeException(String msg, NndlNode node) {
		super(msg, node);
	}

	public NndlFlowBreakerRuntimeException(String msg, NndlNode node, Exception e) {
		super(msg, node, e);
	}

}

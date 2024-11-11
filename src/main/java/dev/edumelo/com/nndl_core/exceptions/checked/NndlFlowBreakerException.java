package dev.edumelo.com.nndl_core.exceptions.checked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlFlowBreakerException extends NndlActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6332928198701423154L;

	public NndlFlowBreakerException(String msg, NndlNode node) {
		super(msg, node);
	}

	public NndlFlowBreakerException(String msg, NndlNode node, Exception e) {
		super(msg, node, e);
	}

}

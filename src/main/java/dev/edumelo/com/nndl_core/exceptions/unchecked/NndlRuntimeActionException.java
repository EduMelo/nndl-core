package dev.edumelo.com.nndl_core.exceptions.unchecked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlRuntimeActionException extends NndlRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1275109293140522052L;
	
	private static String getMessage(String msg, NndlNode node) {
		return msg + (node != null ? node.exceptionMessage() : "");
	}

	public NndlRuntimeActionException(String msg, NndlNode node) {
		super(getMessage(msg, node));
	}

	public NndlRuntimeActionException(String msg, NndlNode node, Throwable e) {
		super(getMessage(msg, node), e);
	}

}

package dev.edumelo.com.nndl_core.exceptions;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlNodeRuntimeException extends NndlRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2000425909941775905L;

	public NndlNodeRuntimeException(String msg, NndlNode node) {
		super(msg);
	}
	
}

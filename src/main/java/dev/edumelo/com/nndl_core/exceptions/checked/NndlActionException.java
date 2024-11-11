package dev.edumelo.com.nndl_core.exceptions.checked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlActionException extends NndlException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1275109293140522052L;
	
	public NndlActionException(String msg, NndlNode node) {
		super(msg + (node != null ? node.exceptionMessage() : ""));
	}
	
	public NndlActionException(String msg, NndlNode node, Throwable e) {
		super(msg + (node != null ? node.exceptionMessage() : ""), e);
	}

}

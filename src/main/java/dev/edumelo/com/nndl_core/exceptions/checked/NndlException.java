package dev.edumelo.com.nndl_core.exceptions.checked;

public class NndlException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4872842738483811032L;

	public NndlException(String msg) {
		super(msg);
	}

	public NndlException(String msg, Throwable e) {
		super(msg, e);
	}
	
}

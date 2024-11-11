package dev.edumelo.com.nndl_core.exceptions.unchecked;

public class NndlRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4872842738483811032L;

	public NndlRuntimeException(String msg) {
		super(msg);
	}

	public NndlRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}
	
}

package dev.edumelo.com.nndl_core.exceptions;

public class NndlFlowBreakerException extends NndlException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6332928198701423154L;

	public NndlFlowBreakerException(String msg) {
		super(msg);
	}

	public NndlFlowBreakerException(String msg, Exception e) {
		super(msg, e);
	}

}

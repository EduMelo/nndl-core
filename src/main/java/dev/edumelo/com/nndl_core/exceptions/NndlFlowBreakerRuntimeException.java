package dev.edumelo.com.nndl_core.exceptions;

public class NndlFlowBreakerRuntimeException extends NndlRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6332928198701423154L;

	public NndlFlowBreakerRuntimeException(String msg) {
		super(msg);
	}

	public NndlFlowBreakerRuntimeException(String msg, Exception e) {
		super(msg, e);
	}

}

package dev.edumelo.com.nndl_core.exceptions;

public class RunBreakerActionNotPerformed extends NndlFlowBreakerRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6012955666080058202L;
	
	public RunBreakerActionNotPerformed(String msg, Exception e) {
		super(msg, e);
	}

	public RunBreakerActionNotPerformed(String msg) {
		super(msg);
	}

}

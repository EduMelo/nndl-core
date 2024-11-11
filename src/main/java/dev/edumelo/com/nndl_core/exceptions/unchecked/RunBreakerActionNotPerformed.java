package dev.edumelo.com.nndl_core.exceptions.unchecked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class RunBreakerActionNotPerformed extends NndlFlowBreakerRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6012955666080058202L;
	
	public RunBreakerActionNotPerformed(String msg, NndlNode node, Exception e) {
		super(msg, node, e);
	}

	public RunBreakerActionNotPerformed(String msg, NndlNode node) {
		super(msg, node);
	}

}

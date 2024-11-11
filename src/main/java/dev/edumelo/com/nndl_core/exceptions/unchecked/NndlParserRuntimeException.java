package dev.edumelo.com.nndl_core.exceptions.unchecked;

import java.util.function.Supplier;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlParserRuntimeException extends NndlRuntimeActionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1911938017657731824L;

	public NndlParserRuntimeException(String msg, NndlNode node) {
		super(msg, node);
	}
	
	public static Supplier<NndlParserRuntimeException> get(String msg, NndlNode node) {
	    return () -> new NndlParserRuntimeException(msg, node);
	}

}

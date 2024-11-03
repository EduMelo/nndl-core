package dev.edumelo.com.nndl_core.exceptions;

import java.util.function.Supplier;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlParserException extends NndlNodeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1911938017657731824L;

	public NndlParserException(String msg, NndlNode node) {
		super(msg, node);
	}
	
	public static Supplier<NndlParserException> get(String msg, NndlNode node) {
	    return () -> new NndlParserException(msg, node);
	}

}

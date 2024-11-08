package dev.edumelo.com.nndl_core.action.landmark;

import dev.edumelo.com.nndl_core.exceptions.NndlActionException;
import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class NndlLandmarkException extends NndlActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1786333319881088312L;
	
	public NndlLandmarkException(String msg, NndlNode node, Throwable e) {
		super(msg, node, e);
	}

}

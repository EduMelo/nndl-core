package dev.edumelo.com.nndl_core.exceptions.checked;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class InfiniteScrollMaxLoopCountReached extends NndlActionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2369511760053282790L;

	public InfiniteScrollMaxLoopCountReached(String msg, NndlNode node) {
		super(msg, node);
	}

}

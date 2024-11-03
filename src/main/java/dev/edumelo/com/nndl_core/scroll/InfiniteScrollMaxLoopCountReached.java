package dev.edumelo.com.nndl_core.scroll;

import dev.edumelo.com.nndl_core.exceptions.ActionException;

public class InfiniteScrollMaxLoopCountReached extends ActionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2369511760053282790L;

	public InfiniteScrollMaxLoopCountReached(String msg) {
		super(msg);
	}

}

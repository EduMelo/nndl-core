package dev.edumelo.com.nndl_core.action;

public class ActionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1275109293140522052L;
	
	public ActionException(String msg) {
		super(msg);
	}
	
	public ActionException(String msg, Throwable e) {
		super(msg, e);
	}

}

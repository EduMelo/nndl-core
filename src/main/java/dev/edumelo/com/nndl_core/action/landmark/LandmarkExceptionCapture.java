package dev.edumelo.com.nndl_core.action.landmark;

import dev.edumelo.com.nndl_core.nndl.NndlNode;

public class LandmarkExceptionCapture {
	private boolean exceptionCaptured;
	private Throwable throwable;
	private NndlNode node;
	
	public boolean isExceptionCaptured() {
		return exceptionCaptured;
	}
	public void setExceptionCaptured(boolean exceptionCaptured) {
		this.exceptionCaptured = exceptionCaptured;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public NndlNode getNode() {
		return node;
	}
	public void setNode(NndlNode node) {
		this.node = node;
	}
	
}

package dev.edumelo.com.nndl_core.action.landmark;

public class LandmarkExceptionCapture {
	private boolean exceptionCaptured;
	private Throwable throwable;
	
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
	
}

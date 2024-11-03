package dev.edumelo.com.nndl_core.action.landmark;

import org.openqa.selenium.By;

public class LandmarkExceptionCapture {
	private boolean exceptionCaptured;
	private Throwable throwable;
	private By locator;
	
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
	public void setLocator(By locator) {
		this.locator = locator;
	}
	public By getLocator() {
		return locator;
	}
	
}

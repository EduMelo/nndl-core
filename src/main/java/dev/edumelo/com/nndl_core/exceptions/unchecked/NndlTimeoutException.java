package dev.edumelo.com.nndl_core.exceptions.unchecked;

public class NndlTimeoutException extends NndlDriverException {

	private static final long serialVersionUID = 1L;

	public NndlTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}

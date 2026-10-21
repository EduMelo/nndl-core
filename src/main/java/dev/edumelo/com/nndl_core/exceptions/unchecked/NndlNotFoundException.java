package dev.edumelo.com.nndl_core.exceptions.unchecked;

public class NndlNotFoundException extends NndlDriverException {

	private static final long serialVersionUID = 1L;

	public NndlNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

package io.hpsf.serialization.api;

/**
 * 
 * @author winflex
 */
public class SerializeException extends Exception {

	private static final long serialVersionUID = 6695247263149098353L;

	public SerializeException() {
		super();
	}

	public SerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SerializeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializeException(String message) {
		super(message);
	}

	public SerializeException(Throwable cause) {
		super(cause);
	}
}

package org.hpsf.registry.api;

/**
 * 
 * @author winflex
 */
public class RegistryException extends Exception {

	private static final long serialVersionUID = 6540009190057552454L;

	public RegistryException() {
		super();
	}

	public RegistryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegistryException(String message) {
		super(message);
	}

	public RegistryException(Throwable cause) {
		super(cause);
	}
}

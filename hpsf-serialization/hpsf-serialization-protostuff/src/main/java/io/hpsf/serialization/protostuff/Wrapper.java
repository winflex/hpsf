package io.hpsf.serialization.protostuff;

/**
 * 
 * 
 * @author winflex
 */
public class Wrapper {

	private Object data;

	public Wrapper(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}
}

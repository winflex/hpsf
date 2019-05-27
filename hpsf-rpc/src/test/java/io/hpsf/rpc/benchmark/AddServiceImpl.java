package io.hpsf.rpc.benchmark;

/**
 * 
 * @author winflex
 */
public class AddServiceImpl implements AddService {

	@Override
	public int add(int a, int b) {
		return a + b;
	}
}

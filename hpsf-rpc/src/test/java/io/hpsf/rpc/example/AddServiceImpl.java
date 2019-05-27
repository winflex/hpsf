package io.hpsf.rpc.example;

/**
 * 
 * @author winflex
 */
public class AddServiceImpl implements AddService {

	@Override
	public int add(int a, int b) {
//		throw new RuntimeException("xxxxxxxxxxxxx");
		return a + b;
	}
}

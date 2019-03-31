/**
 * 
 */
package lrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lrpc.util.URL;

/**
 *
 * @author winflex
 */
public class InvokeInvocationHandler implements InvocationHandler {

	private RpcClient rpcClient;
	private final URL url;
	
	
	public InvokeInvocationHandler(RpcClient rpcClient, URL url) {
		this.rpcClient = rpcClient;
		this.url = url;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}

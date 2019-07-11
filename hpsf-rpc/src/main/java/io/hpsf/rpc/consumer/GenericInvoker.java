package io.hpsf.rpc.consumer;

import io.hpsf.rpc.Invocation;
import io.hpsf.rpc.RpcException;

/**
 * 
 * @author winflex
 */
public class GenericInvoker extends DefaultInvoker<GenericService> {

	// 泛化接口
	private final String iface;
	
	public GenericInvoker(String iface, RpcClient rpcClient) throws RpcException {
		super(GenericService.class, rpcClient);
		this.iface = iface;
	}

	@Override
	public Object invoke(Invocation inv) throws Throwable {
		if (!isGenericInvocation(inv)) {
			throw new IllegalArgumentException("Not a generic invocation " + inv);
		}
		
		String methodName = (String) inv.getParemeters()[0];
		Class<?>[] paramTypes = (Class<?>[]) inv.getParemeters()[1];
		Object[] params = (Object[]) inv.getParemeters()[2];
		
		inv.setClassName(iface);
		inv.setMethodName(methodName);
		inv.setParameterTypes(paramTypes);
		inv.setParemeters(params);
		
		return super.invoke(inv);
	}

	private boolean isGenericInvocation(Invocation inv) {
		return inv.getClassName().equals(GenericService.class.getName()) && inv.getMethodName().equals("$invoke")
				&& inv.getParemeters().length == 3;
	}
}

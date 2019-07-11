package io.hpsf.rpc.consumer;

import io.hpsf.rpc.RpcException;

/**
 * 泛化调用接口
 * 
 * @author winflex
 */
public interface GenericService {
	
	Object $invoke(String method, Class<?>[] parameterTypes, Object[] args) throws RpcException;
	
}

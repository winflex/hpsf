package io.hpsf.rpc.client;

import io.hpsf.rpc.common.RpcException;

/**
 * 泛化调用接口
 * 
 * @author winflex
 */
public interface GenericService {
	
	Object $invoke(String method, Class<?>[] parameterTypes, Object[] args) throws RpcException;
	
}

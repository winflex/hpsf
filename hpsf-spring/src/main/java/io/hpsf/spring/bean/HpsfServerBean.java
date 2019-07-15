package io.hpsf.spring.bean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import io.hpsf.rpc.provider.RpcServer;
import io.hpsf.rpc.provider.RpcServerConfig;

/**
 * 
 * @author winflex
 */
public class HpsfServerBean extends RpcServerConfig implements InitializingBean, DisposableBean {

	private RpcServer rpcServer;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.rpcServer = new RpcServer(this);
	}

	public final RpcServer getRpcServer() {
		return rpcServer;
	}

	@Override
	public void destroy() throws Exception {
		rpcServer.close();
	}
}

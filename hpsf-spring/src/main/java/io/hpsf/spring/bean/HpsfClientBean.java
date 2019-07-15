package io.hpsf.spring.bean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import io.hpsf.rpc.consumer.RpcClient;
import io.hpsf.rpc.consumer.RpcClientConfig;

/**
 * 
 * @author winflex
 */
public class HpsfClientBean extends RpcClientConfig implements InitializingBean, DisposableBean {

	private RpcClient client;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		client = new RpcClient(this);
	}
	
	@Override
	public void destroy() throws Exception {
		client.close();
	}

	public final RpcClient getClient() {
		return client;
	}
}

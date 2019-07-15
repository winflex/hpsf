package io.hpsf.spring.bean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import io.hpsf.rpc.consumer.GenericService;

/**
 * 
 * @author winflex
 */
public class HpsfReferenceBean implements InitializingBean, FactoryBean<Object> {

	private String iface; // 接口类名
	private String version;
	private boolean generic;
	private HpsfClientBean client;

	private Object proxy;
	private Class<?> ifaceClass;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (generic) {
			proxy = client.getClient().getGenericServiceProxy(iface, version);
		} else {
			ifaceClass = Class.forName(iface);
			proxy = client.getClient().getServiceProxy(ifaceClass, version);
		}
	}

	@Override
	public Object getObject() throws Exception {
		return proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return generic ? GenericService.class : ifaceClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public final void setInterface(String iface) {
		this.iface = iface;
	}

	public final void setVersion(String version) {
		this.version = version;
	}

	public final void setGeneric(boolean generic) {
		this.generic = generic;
	}

	public final void setClient(HpsfClientBean client) {
		this.client = client;
	}
}

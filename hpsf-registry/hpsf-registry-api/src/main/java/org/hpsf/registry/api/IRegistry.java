package org.hpsf.registry.api;

import java.util.List;

import io.hpsf.common.lifecycle.ILifeCycle;

/**
 * 注册中心
 * 
 * @author winflex
 */
public interface IRegistry extends ILifeCycle{
	
	void register(Registration registration);
	
	void unregister(Registration registration);
	
	void subscribe(ServiceMeta serviceMeta, INotifyListener listener);
	
	void unsubscribe(ServiceMeta serviceMeta, INotifyListener listener);
	
	List<Registration> lookup(ServiceMeta serviceMeta);
}

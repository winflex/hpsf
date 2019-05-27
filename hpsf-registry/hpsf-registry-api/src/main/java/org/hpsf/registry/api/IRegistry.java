package org.hpsf.registry.api;

import java.util.List;

/**
 * 注册中心
 * 
 * @author lixiaohui
 */
public interface IRegistry {
	
	void register(Registration registration);
	
	void unregister(Registration registration);
	
	void subscribe(ServiceMeta serviceMeta, INotifyListener listener);
	
	void unsubscribe(ServiceMeta serviceMeta, INotifyListener listener);
	
	List<Registration> lookup(ServiceMeta serviceMeta);
}

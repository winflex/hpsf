package org.hpsf.registry.api;

import java.io.Closeable;
import java.util.List;

/**
 * 注册中心
 * 
 * @author winflex
 */
public interface IRegistry extends Closeable {

	void init(RegistryConfig config) throws RegistryException;
	
	void register(Registration registration) throws RegistryException;
	
	void unregister(Registration registration) throws RegistryException;
	
	void subscribe(ServiceMeta serviceMeta, INotifyListener listener) throws RegistryException;
	
	void unsubscribe(ServiceMeta serviceMeta, INotifyListener listener) throws RegistryException;
	
	List<Registration> lookup(ServiceMeta serviceMeta) throws RegistryException;
	
}

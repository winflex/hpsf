package io.hpsf.registry.api;

import java.io.Closeable;
import java.util.List;

/**
 * 注册中心
 * 
 * @author winflex
 */
public interface Registry extends Closeable {

	void init(String connectString) throws RegistryException;
	
	void register(Registration registration) throws RegistryException;
	
	void unregister(Registration registration) throws RegistryException;
	
	void subscribe(ServiceMeta serviceMeta, NotifyListener listener) throws RegistryException;
	
	void unsubscribe(ServiceMeta serviceMeta, NotifyListener listener) throws RegistryException;
	
	List<Registration> lookup(ServiceMeta serviceMeta) throws RegistryException;
	
}

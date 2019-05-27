package io.hpsf.registry.zookeeper;

import java.util.List;

import org.hpsf.registry.api.INotifyListener;
import org.hpsf.registry.api.IRegistry;
import org.hpsf.registry.api.ServiceMeta;

import io.hpsf.common.lifecycle.AbstractLifeCycle;
import io.hpsf.common.lifecycle.LifeCycleException;

import org.hpsf.registry.api.Registration;

/**
 * 
 * @author winflex
 */
public class ZookeeperServiceRegistry extends AbstractLifeCycle implements IRegistry {

	@Override
	public void register(Registration registration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregister(Registration registration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribe(ServiceMeta serviceMeta, INotifyListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribe(ServiceMeta serviceMeta, INotifyListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Registration> lookup(ServiceMeta serviceMeta) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void destroyInternal() throws LifeCycleException {
		super.destroyInternal();
		// TODO Auto-generated method stub
	}
}

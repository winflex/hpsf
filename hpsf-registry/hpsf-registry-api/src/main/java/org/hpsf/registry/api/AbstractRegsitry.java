package org.hpsf.registry.api;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hpsf.registry.api.NotifyListener.NotifyType;

import io.hpsf.common.ConcurrentSet;

/**
 * 
 * @author winflex
 */
public abstract class AbstractRegsitry implements Registry {

	// 已注册信息
	private final ConcurrentSet<Registration> registrations = new ConcurrentSet<>();
	// 已订阅信息
	private final ConcurrentMap<ServiceMeta, ConcurrentSet<NotifyListener>> subscribers = new ConcurrentHashMap<>();
	
	@Override
	public void register(Registration registration) throws RegistryException {
		registrations.add(registration);
		doRegister(registration);
	}
	
	protected abstract void doRegister(Registration registration) throws RegistryException;

	@Override
	public void unregister(Registration registration) throws RegistryException {
		registrations.remove(registration);
		doUnregister(registration);
	}
	
	protected abstract void doUnregister(Registration registration) throws RegistryException;
	
	@Override
	public void subscribe(ServiceMeta serviceMeta, NotifyListener listener) throws RegistryException {
		ConcurrentSet<NotifyListener> listeners = subscribers.get(serviceMeta);
		if (listeners == null) {
			listeners = new ConcurrentSet<>();
			ConcurrentSet<NotifyListener> oldListeners = subscribers.putIfAbsent(serviceMeta, listeners);
			if (oldListeners != null) {
				listeners = oldListeners;
			}
		}
		listeners.add(listener);

		doSubscribe(serviceMeta);
	}

	protected abstract void doSubscribe(ServiceMeta serviceMeta) throws RegistryException;

	@Override
	public void unsubscribe(ServiceMeta serviceMeta, NotifyListener listener) throws RegistryException {
		ConcurrentSet<NotifyListener> listeners = subscribers.get(serviceMeta);
		if (listeners != null) {
			listeners.remove(listener);
		}

		doUnsubscribe(serviceMeta);
	}

	protected abstract void doUnsubscribe(ServiceMeta serviceMeta) throws RegistryException;

	protected final void notify(Registration r, NotifyType type) {
		subscribers.get(r.getServiceMeta()).forEach(l -> l.notify(r, type));
	}

	public final Set<Registration> getRegistrations() {
		return Collections.unmodifiableSet(registrations);
	}

	public final Map<ServiceMeta, ConcurrentSet<NotifyListener>> getSubscribers() {
		return Collections.unmodifiableMap(subscribers);
	}
}

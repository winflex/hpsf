package io.hpsf.registry.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.hpsf.common.ConcurrentSet;
import io.hpsf.registry.api.NotifyListener.NotifyType;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author winflex
 */
@Slf4j
public abstract class AbstractRegsitry implements Registry {

	// 已注册信息
	private final ConcurrentSet<Registration> registrations = new ConcurrentSet<>();
	// 已订阅信息
	private final ConcurrentMap<ServiceMeta, ConcurrentSet<NotifyListener>> subscribers = new ConcurrentHashMap<>();

	private final ConcurrentMap<ServiceMeta, ConcurrentSet<Registration>> lookupCache = new ConcurrentHashMap<>();

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
		// remove self from lookup cache
		ConcurrentSet<Registration> registrations = lookupCache.get(registration.getServiceMeta());
		if (registrations != null) {
			registrations.remove(registration);
		}
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

	@Override
	public List<Registration> lookup(ServiceMeta serviceMeta) throws RegistryException {
		ConcurrentSet<Registration> registrations = lookupCache.get(serviceMeta);
		if (registrations == null) {
			synchronized (this) {
				if ((registrations = lookupCache.get(serviceMeta)) == null) {
					// real lookup
					registrations = new ConcurrentSet<>(doLookup(serviceMeta));

					// 订阅该服务以便服务上下线时刷新本地lookup cache
					final ConcurrentSet<Registration> finaled = registrations;
					subscribe(serviceMeta, (registration, notifyType) -> {
						if (notifyType == NotifyType.ONLINE) {
							finaled.add(registration);
						} else if (notifyType == NotifyType.OFFLINE) {
							finaled.remove(registration);
						}
					});
					lookupCache.put(serviceMeta, registrations);
				}
			}
		}
		return new ArrayList<>(registrations);
	}

	protected abstract List<Registration> doLookup(ServiceMeta serviceMeta) throws RegistryException;

	protected final void notify(Registration r, NotifyType type) {
		log.info("{} {}", r, type.toString().toLowerCase());
		subscribers.get(r.getServiceMeta()).forEach(l -> l.notify(r, type));
	}

	@Override
	public final void close() {
		lookupCache.clear();
		// 下线所有服务
		registrations.forEach(r -> {
			try {
				unregister(r);
			} catch (RegistryException e) {
				log.error(e.getMessage(), e);
			}
		});
		registrations.clear();
		
		// 取消所有订阅
		subscribers.forEach((serviceMeta, listeners) -> {
			listeners.forEach(listener -> {
				try {
					unsubscribe(serviceMeta, listener);
				} catch (RegistryException e) {
					log.error(e.getMessage(), e);
				}
			});
		});
		subscribers.clear();
		
		doClose();
	}

	protected abstract void doClose();

	protected final Set<Registration> getRegistrations() {
		return Collections.unmodifiableSet(registrations);
	}

	protected final Map<ServiceMeta, ConcurrentSet<NotifyListener>> getSubscribers() {
		return Collections.unmodifiableMap(subscribers);
	}
}

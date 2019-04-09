package lrpc.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

/**
 * 本地注册中心实现
 * 
 * @author winflex
 */
public class DefaultRegistryCenter implements IRegistryCenter {
	
	protected final ConcurrentMap<String, Registry> registries = new ConcurrentHashMap<>();
	
	@Override
	public void register(Class<?> iface, Object instance, Executor executor) {
		Registry newRegistry = new Registry(iface, instance, executor);
		if (registries.putIfAbsent(iface.getName(), newRegistry) != null) {
			throw new RuntimeException("Already registered " + iface);
		}
	}
	
	@Override
	public Registry get(String iface) {
		return registries.get(iface);
	}
}

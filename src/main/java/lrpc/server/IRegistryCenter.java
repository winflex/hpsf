package lrpc.server;

import java.util.concurrent.Executor;

import lrpc.util.concurrent.SynchronousExecutor;

/**
 * 服务注册中心
 * 
 * @author winflex
 */
public interface IRegistryCenter {

	default void register(Class<?> iface, Object instance) {
		register(iface, instance, SynchronousExecutor.INSTANCE);
	}

	void register(Class<?> iface, Object instance, Executor executor);

	default Registry get(Class<?> iface) {
		return get(iface.getName());
	}
	
	Registry get(String iface);

	public static final class Registry {
		private final Class<?> iface;
		private final Object instance;
		private final Executor executor;

		public Registry(Class<?> iface, Object instance, Executor executor) {
			this.iface = iface;
			this.instance = instance;
			this.executor = executor;
		}

		public Class<?> getInterface() {
			return iface;
		}

		public Object getInstance() {
			return instance;
		}

		public Executor getExecutor() {
			return executor;
		}
	}
}

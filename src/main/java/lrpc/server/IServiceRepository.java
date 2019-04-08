package lrpc.server;

import java.util.concurrent.Executor;

import lrpc.util.concurrent.SynchronousExecutor;

/**
 * 服务仓库
 * 
 * @author winflex
 */
public interface IServiceRepository {

	default void publish(Class<?> iface, Object instance) {
		publish(iface, instance, SynchronousExecutor.INSTANCE);
	}

	void publish(Class<?> iface, Object instance, Executor executor);

	Publishment get(String iface);

	default Publishment get(Class<?> iface) {
		return get(iface.getName());
	}

	public static final class Publishment {
		private final Class<?> iface;
		private final Object instance;
		private final Executor executor;

		public Publishment(Class<?> iface, Object instance, Executor executor) {
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

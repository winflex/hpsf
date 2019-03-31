package lrpc.server;

import java.util.concurrent.Executor;

import lrpc.util.concurrent.SynchronousExecutor;

/**
 * 
 * @author winflex
 */
public interface IServiceRepository {

	default void publish(String iface, Object instance) {
		publish(iface, instance, SynchronousExecutor.INSTANCE);
	}

	void publish(String iface, Object instance, Executor executor);

	Publishment get(String iface);

	public static final class Publishment {
		private final String className;
		private final Object instance;
		private final Executor executor;

		public Publishment(String className, Object instance, Executor executor) {
			this.className = className;
			this.instance = instance;
			this.executor = executor;
		}

		public String getClassName() {
			return className;
		}

		public Object getInstance() {
			return instance;
		}

		public Executor getExecutor() {
			return executor;
		}
	}
}

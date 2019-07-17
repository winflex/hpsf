package io.hpsf.spring.bean;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import io.hpsf.common.concurrent.NamedThreadFactory;

/**
 * 
 * @author winflex
 */
public class HpsfServiceBean implements InitializingBean, DisposableBean {

	private String id;
	private HpsfServerBean server;
	private Object ref;
	private String version;
	private String iface;

	// thread pool configs
	private int corePoolSize;
	private int maxPoolSize;
	private int queueSize;
	private String threadName;
	private int keepAliveTime; // in seconds
	private boolean allowCoreThreadTimeout;

	private Class<?> theInterface;

	@Override
	public void afterPropertiesSet() throws Exception {
		theInterface = findInterface();

		if (corePoolSize > 0 && maxPoolSize > 0 && corePoolSize <= maxPoolSize) {
			if (threadName == null) {
				threadName = (id == null || id.isEmpty()) ? ref.getClass().getSimpleName() : id;
			}
			if (queueSize <= 0) {
				queueSize = Integer.MAX_VALUE;
			}
			if (keepAliveTime <= 0) {
				keepAliveTime = 60;
			}
			ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>(queueSize), new NamedThreadFactory(threadName));
			if (allowCoreThreadTimeout) {
				executor.allowCoreThreadTimeOut(true);
			}
			server.getRpcServer().publish(theInterface, ref, version, executor);
		} else {
			server.getRpcServer().publish(theInterface, ref, version);
		}
	}

	private Class<?> findInterface() throws ClassNotFoundException {
		Class<?>[] ifaces = ref.getClass().getInterfaces();
		if (ifaces.length == 0) {
			throw new RuntimeException(ref.getClass().getName() + " does not implement any interfaces");
		} else if (ifaces.length == 1) {
			return ifaces[0];
		} else {
			if (iface == null) {
				throw new RuntimeException("iface property is required");
			}
			Class<?> theIface = Class.forName(iface);
			for (Class<?> iface : ifaces) {
				if (iface == theIface) {
					return theIface;
				}
			}
			throw new RuntimeException(ref.getClass().getName() + " does not implement " + iface);
		}
	}

	@Override
	public void destroy() throws Exception {
		server.getRpcServer().unpublish(theInterface, ref, version);
	}

	public final void setServer(HpsfServerBean server) {
		this.server = server;
	}

	public final void setRef(Object ref) {
		this.ref = ref;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public final void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public final void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public final void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public final void setKeepAliveTime(int keepAlive) {
		this.keepAliveTime = keepAlive;
	}

	public final void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
		this.allowCoreThreadTimeout = allowCoreThreadTimeout;
	}

	public final void setTheInterface(Class<?> theInterface) {
		this.theInterface = theInterface;
	}

	public final void setIface(String iface) {
		this.iface = iface;
	}

	public final void setVersion(String version) {
		this.version = version;
	}

	public final void setInterface(String iface) {
		this.iface = iface;
	}
}

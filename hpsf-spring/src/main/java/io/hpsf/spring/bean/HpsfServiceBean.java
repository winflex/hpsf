package io.hpsf.spring.bean;

import java.util.concurrent.Executor;
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
	private int coreThreads;
	private int maxThreads;
	private String version;
	private String iface;

	private Class<?> theInterface;

	@Override
	public void afterPropertiesSet() throws Exception {
		theInterface = findInterface();
		
		final String threadName = (id == null || id.isEmpty()) ? ref.getClass().getSimpleName() : id;
		if (coreThreads > 0 && maxThreads > 0 && coreThreads <= maxThreads) {
			Executor executor = new ThreadPoolExecutor(coreThreads, maxThreads, 1, TimeUnit.MINUTES,
					new LinkedBlockingQueue<>(), new NamedThreadFactory(threadName));
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

	public final void setCoreThreads(int coreThreads) {
		this.coreThreads = coreThreads;
	}

	public final void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
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

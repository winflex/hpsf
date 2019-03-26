package lrpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 
 * @author winflex
 */
public class ServicePublisher implements IServicePublisher {
	
	protected final Map<String, Publishment> publishments = new HashMap<>();
	
	@Override
	public synchronized void publish(String iface, Object instance, Executor executor) {
		if (publishments.containsKey(iface)) {
			throw new RuntimeException("duplicated publishment of " + iface);
		}
		
		publishments.put(iface, new Publishment(iface, instance, executor));
	}
	
	
	public final static class Publishment {
		public final String iface;
		public final Object instance;
		public final Executor executor;
		
		public Publishment(String iface, Object instance, Executor executor) {
			this.iface = iface;
			this.instance = instance;
			this.executor = executor;
		}
	}
}

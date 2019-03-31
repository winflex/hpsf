package lrpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 
 * @author winflex
 */
public class ServiceRepository implements IServiceRepository {
	
	protected final Map<String, Publishment> publishments = new HashMap<>();
	
	@Override
	public synchronized void publish(String iface, Object instance, Executor executor) {
		if (publishments.containsKey(iface)) {
			throw new RuntimeException("duplicated publishment of " + iface);
		}
		
		publishments.put(iface, new Publishment(iface, instance, executor));
	}
	
	@Override
	public Publishment get(String iface) {
		return publishments.get(iface);
	}
}

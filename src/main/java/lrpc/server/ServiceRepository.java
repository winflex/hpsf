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
	public synchronized void publish(Class<?> iface, Object instance, Executor executor) {
		String ifaceName = iface.getName();
		if (publishments.containsKey(ifaceName)) {
			throw new RuntimeException("Already published" + iface);
		}
		
		publishments.put(iface.getName(), new Publishment(iface, instance, executor));
	}
	
	@Override
	public Publishment get(String iface) {
		return publishments.get(iface);
	}
}

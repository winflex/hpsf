package io.hpsf.rpc.consumer.balance;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.hpsf.common.util.LockGroup;
import io.hpsf.registry.api.ServiceMeta;

/**
 * 为每个服务维护自己的负载均衡
 * 
 * @author winflex
 */
public class LoadBalancerManager {
	
	private final ConcurrentMap<ServiceMeta, LoadBalancer> loadBalancers = new ConcurrentHashMap<>();
	private final LockGroup<ServiceMeta> lockGroup = new LockGroup<>();
	
	public LoadBalancer getLoadBalancer(ServiceMeta serviceMeta) {
		LoadBalancer loadBalancer = loadBalancers.get(serviceMeta);
		if (loadBalancer == null) {
			synchronized (lockGroup.getLock(serviceMeta)) {
				if ((loadBalancer = loadBalancers.get(serviceMeta)) == null) {
					loadBalancers.put(serviceMeta, loadBalancer = new RoundRobinLoadBalancer());
				}
			}
		}
		return loadBalancer;
	}
}

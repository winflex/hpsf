package io.hpsf.rpc.consumer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import io.hpsf.common.Endpoint;
import io.hpsf.common.util.LockGroup;
import io.hpsf.rpc.consumer.ChannelBag.HealthChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

/**
 * 维护多个server的连接
 * 
 * @author winflex
 */
public class ChannelManager {
	
	private final Bootstrap bootstrap;
	private final int maxConnectionPerServer;
	
	private final ConcurrentMap<Endpoint, ChannelBag> channelGroups = new ConcurrentHashMap<>();
	private final LockGroup<Endpoint> endpointLockGroup = new LockGroup<>();
	
	public ChannelManager(Bootstrap bootstrap, int maxConnectionPerServer) {
		this.bootstrap = bootstrap;
		this.maxConnectionPerServer = maxConnectionPerServer;
	}
	
	public Channel getChannel(Endpoint endpoint, int timeoutMillis) throws TimeoutException, IOException {
		ChannelBag group = channelGroups.get(endpoint);
		if (group == null) {
			synchronized (endpointLockGroup.getLock(endpoint)) {
				if ((group = channelGroups.get(endpoint)) == null) {
					channelGroups.put(endpoint, group = newChannelGroup(endpoint));
				}
			}
		}
		return group.getChannel(timeoutMillis);
	}
	
	private ChannelBag newChannelGroup(Endpoint endpoint) throws IOException {
		Bootstrap bootstrap = this.bootstrap.clone();
		bootstrap.remoteAddress(endpoint.toSocketAddress());
		return new ChannelBag(bootstrap, maxConnectionPerServer, HealthChecker.ACTIVE);
	}
	
	public void close() {
		channelGroups.values().forEach(group -> group.close());
	}
}

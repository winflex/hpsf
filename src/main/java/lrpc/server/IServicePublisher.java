package lrpc.server;

import java.util.concurrent.Executor;

/**
 * 
 * @author winflex
 */
public interface IServicePublisher {
	
	default void publish(String iface, Object instance) {
		publish(iface, instance, new Executor() {
			
			@Override
			public void execute(Runnable command) {
				command.run();
			}
		});
	}
	
	void publish(String iface, Object instance, Executor executor);
	
}

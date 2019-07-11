package io.hpsf.rpc.consumer.balance;

import java.util.List;

/**
 * 
 * @author winflex
 */
public interface LoadBalancer {
	
	<T> T select(List<T> elements);
	
}

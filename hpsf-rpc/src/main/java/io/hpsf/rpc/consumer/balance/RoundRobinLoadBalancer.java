package io.hpsf.rpc.consumer.balance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author winflex
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

	private final AtomicInteger current = new AtomicInteger();

	@Override
	protected <T> T doSelect(List<T> elements) {
		int index = (current.getAndIncrement() & Integer.MAX_VALUE) % elements.size();
		return elements.get(index);
	}
}

package io.hpsf.rpc.consumer.balance;

import java.util.List;

/**
 * 
 * @author winflex
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

	@Override
	public <T> T select(List<T> elements) {
		if (elements.isEmpty()) {
			return null;
		}
		if (elements.size() == 1) {
			return elements.get(0);
		}
		return doSelect(elements);
	}

	protected abstract <T> T doSelect(List<T> elements);

}

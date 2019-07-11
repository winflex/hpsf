package io.hpsf.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author winflex
 */
public class LockGroup<E> {
	
	private final ConcurrentMap<E, Object> locks = new ConcurrentHashMap<>();
	
	public final Object getLock(E key) {
		Object lock = locks.get(key);
		if (lock == null) {
			Object oldLock = locks.putIfAbsent(key, lock = new Object());
			if (oldLock != null) {
				lock = oldLock;
			}
		}
		return lock;
	}
}

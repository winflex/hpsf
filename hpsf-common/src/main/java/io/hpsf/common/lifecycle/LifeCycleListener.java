package io.hpsf.common.lifecycle;

import java.util.EventListener;

/**
 * 
 * 
 * @author winflex
 */
public interface LifeCycleListener extends EventListener {
	
	void lifeCycleEvent(LifeCycleEvent e);
}

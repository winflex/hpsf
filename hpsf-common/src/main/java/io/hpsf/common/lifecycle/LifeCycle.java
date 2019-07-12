package io.hpsf.common.lifecycle;

/**
 * 生命周期抽象
 * 
 * @author winflex
 */
public interface LifeCycle {
	
	LifeCycle init() throws LifeCycleException;
	
	LifeCycle start() throws LifeCycleException;
	
	LifeCycle destroy() throws LifeCycleException;
	
	LifeCycle addLifeCycleListener(LifeCycleListener l);
	
	LifeCycle removeLifeCycleListener(LifeCycleListener l);
}

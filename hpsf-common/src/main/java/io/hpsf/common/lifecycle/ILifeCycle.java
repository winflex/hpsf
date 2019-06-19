package io.hpsf.common.lifecycle;

/**
 * 生命周期抽象
 * 
 * @author winflex
 */
public interface ILifeCycle {
	
	ILifeCycle init() throws LifeCycleException;
	
	ILifeCycle start() throws LifeCycleException;
	
	ILifeCycle destroy() throws LifeCycleException;
	
	ILifeCycle addLifeCycleListener(ILifeCycleListener l);
	
	ILifeCycle removeLifeCycleListener(ILifeCycleListener l);
}

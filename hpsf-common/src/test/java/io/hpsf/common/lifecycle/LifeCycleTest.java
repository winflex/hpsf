package io.hpsf.common.lifecycle;

/**
 * 
 * @author winflex
 */
public class LifeCycleTest extends DefaultLifeCycle {
	public static void main(String[] args) throws LifeCycleException {
		LifeCycleTest l = new LifeCycleTest();
		l.init();
//		l.start();
		l.destroy();
	}
}

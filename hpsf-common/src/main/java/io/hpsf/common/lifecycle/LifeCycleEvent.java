package io.hpsf.common.lifecycle;

import java.util.EventObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 
 * @author winflex
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LifeCycleEvent extends EventObject {

	private static final long serialVersionUID = 6757600267753576331L;

	private DefaultLifeCycle lifeCycle;
	private LifeCycleState state;
	
	public LifeCycleEvent(Object source, DefaultLifeCycle lifeCycle, LifeCycleState state) {
		super(source);
		this.lifeCycle = lifeCycle;
		this.state = state;
	}
}

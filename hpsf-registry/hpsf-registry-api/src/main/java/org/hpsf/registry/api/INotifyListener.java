package org.hpsf.registry.api;

import java.util.List;

/**
 * 
 * @author winflex
 */
public interface INotifyListener {
	
	void notify(List<Registration> registrations);
}

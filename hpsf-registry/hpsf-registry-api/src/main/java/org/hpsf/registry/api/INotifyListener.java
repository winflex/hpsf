package org.hpsf.registry.api;

import java.util.List;

/**
 * 
 * @author lixiaohui
 */
public interface INotifyListener {
	
	void notify(List<Registration> registrations);
}

package org.hpsf.registry.api;

/**
 * 
 * @author winflex
 */
public interface INotifyListener {

	void notify(Registration registration, NotifyType notifyType);

	public static enum NotifyType {
		ONLINE, OFFLINE;
	}
}

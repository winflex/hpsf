package lrpc.common;

import java.io.Serializable;

/**
 * 
 * @author winflex
 */
public class ServerInfo implements Serializable {
	
	private static final long serialVersionUID = -6460424539998663442L;
	
	private int heartbeatIntervalMillis;

	public int getHeartbeatIntervalMillis() {
		return heartbeatIntervalMillis;
	}

	public void setHeartbeatIntervalMillis(int heartbeatIntervalMillis) {
		this.heartbeatIntervalMillis = heartbeatIntervalMillis;
	}
}

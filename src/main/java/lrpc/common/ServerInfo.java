package lrpc.common;

/**
 * 
 * @author winflex
 */
public class ServerInfo {
	
	private int heartbeatIntervalMillis;

	public int getHeartbeatIntervalMillis() {
		return heartbeatIntervalMillis;
	}

	public void setHeartbeatIntervalMillis(int heartbeatIntervalMillis) {
		this.heartbeatIntervalMillis = heartbeatIntervalMillis;
	}
}

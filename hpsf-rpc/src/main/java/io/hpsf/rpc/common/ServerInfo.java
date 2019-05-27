package io.hpsf.rpc.common;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class ServerInfo implements Serializable {
	
	private static final long serialVersionUID = -6460424539998663442L;
	
	private int heartbeatIntervalMillis;
}

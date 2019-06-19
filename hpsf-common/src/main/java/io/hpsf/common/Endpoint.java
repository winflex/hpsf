package io.hpsf.common;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class Endpoint implements Serializable {

	private static final long serialVersionUID = 6780623948082682620L;

	private String ip;
	private int port;

	public Endpoint() {
	}

	public Endpoint(String address, int port) {
		this.ip = address;
		this.port = port;
	}

	public InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(this.ip, this.port);
	}

	public SocketAddress toSocketAddress() {
		return new InetSocketAddress(ip, port);
	}
}

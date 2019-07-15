package io.hpsf.rpc.provider;

import java.io.IOException;
import java.util.Properties;

import io.hpsf.common.config.Config;
import io.hpsf.common.config.PropertiesConfig;

/**
 * 
 * @author winflex
 */
public class ClassPathPropertiesRpcServerConfig extends RpcServerConfig {

	public ClassPathPropertiesRpcServerConfig(String file) throws IOException {
		Properties p = new Properties();
		p.load(getClass().getClassLoader().getResourceAsStream(file));
		load(new PropertiesConfig(p));
	}

	private void load(Config c) {
		setIp(c.getString("server.rpc.ip", null));
		setPort(c.getInt("server.rpc.port", DEFAULT_PORT));
		setIoThreads(c.getInt("server.rpc.ioThreads", DEFAULT_IO_THREADS));
		setHeartbeatInterval(c.getInt("server.rpc.heartbeatInterval", DEFAULT_HEARTBEAT_INTERVAL));
		setRegistry(c.getString("server.registry"));
	}
}

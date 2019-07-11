package io.hpsf.rpc.provider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import io.hpsf.common.config.Config;
import io.hpsf.common.config.PropertiesConfig;
import io.hpsf.registry.api.RegistryConfig;

/**
 * 
 * @author winflex
 */
public class ClassPathPropertiesRpcServerConfig extends RpcServerConfig {

	public ClassPathPropertiesRpcServerConfig(String file) throws IOException {
		Properties p = new Properties();
		p.load(new FileInputStream(file));
		load(new PropertiesConfig(p));
	}

	private void load(Config c) {
		setIp(c.getString("server.rpc.ip", DEFAULT_IP));
		setPort(c.getInt("server.rpc.port", DEFAULT_PORT));
		setIoThreads(c.getInt("server.rpc.ioThreads", DEFAULT_IO_THREADS));
		setHeartbeatInterval(c.getInt("server.rpc.heartbeatInterval", DEFAULT_HEARTBEAT_INTERVAL));
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setType(c.getString("server.registry.type"));
		registryConfig.setConnectString(c.getString("server.registry.connectString"));
		setRegistryConfig(registryConfig);
	}

}

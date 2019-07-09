package io.hpsf.rpc.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hpsf.registry.api.RegistryConfig;

import io.hpsf.common.config.Config;
import io.hpsf.common.config.PropertiesConfig;

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

		Properties registryProperties = new Properties();
		c.keys().forEach(k -> {
			if (k.startsWith("server.registry.")) {
				registryProperties.setProperty(k.replaceFirst("server.registry.", ""), c.getString(k));
			}
		});
		setRegistryConfig(new RegistryConfig(registryProperties));
	}

}

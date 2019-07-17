package io.hpsf.rpc.provider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.hpsf.common.config.Config;
import io.hpsf.common.config.PropertiesConfig;

/**
 * 
 * @author winflex
 */
public class PropertiesRpcServerConfig extends RpcServerConfig {

	private static final String CLASSPATH_PREFIX = "classpath:";
	
	public PropertiesRpcServerConfig(String file) throws IOException {
		file = file.trim();
		InputStream in;
		if (file.startsWith(CLASSPATH_PREFIX)) {
			in = getClass().getResourceAsStream(file.substring(CLASSPATH_PREFIX.length()));
		} else {
			in = new FileInputStream(file);
		}
		Properties p = new Properties();
		p.load(in);
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

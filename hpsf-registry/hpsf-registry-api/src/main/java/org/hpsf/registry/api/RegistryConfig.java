package org.hpsf.registry.api;

import java.util.Properties;

import io.hpsf.common.config.PropertiesConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 * server.properties:
 * server.registry.name=zookeeper
 * server.registry.server=127.0.0.1:2181
 * 
 * 对应RegistryConfig:
 * get("name") -> "zookeeper"
 * get("server") -> "127.0.0.1:2181"
 * </pre>
 * 
 * @author winflex
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RegistryConfig extends PropertiesConfig {

	public RegistryConfig() {
		super(new Properties());
	}
	
	public RegistryConfig(Properties properties) {
		super(properties);
	}
}

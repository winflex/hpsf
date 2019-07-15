package io.hpsf.registry.api;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class RegistryConfig {

	private String type;

	private String connectString;

	// zookeeper://localhost:2181,localhost:2182,localhost:2183
	public static RegistryConfig parse(String url) {
		String[] parts = url.trim().split("://");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Malformed url " + url);
		}

		RegistryConfig config = new RegistryConfig();
		config.setType(parts[0]);
		config.setConnectString(parts[1]);
		return config;
	}
}

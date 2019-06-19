package io.hpsf.common.config;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * 
 * @author winflex
 */
public class PropertiesConfig extends AbstractConfig {

	private final Properties properties;

	public PropertiesConfig(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String getString(String key) {
		return properties.getProperty(key);
	}

	@Override
	public IConfig setString(String key, String value) {
		properties.setProperty(key, value);
		return this;
	}
	
	@Override
	public String getString(String key, String def) {
		return properties.getProperty(key, def);
	}

	@Override
	public Set<String> keys() {
		final Set<String> keys = new HashSet<>();
		properties.keySet().forEach(k -> keys.add(k.toString()));
		return keys;
	}

}

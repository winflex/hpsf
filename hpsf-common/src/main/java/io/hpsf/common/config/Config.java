package io.hpsf.common.config;

import java.util.Set;

/**
 * 
 * @author winflex
 */
public interface Config {

	String getString(String key);
	String getString(String key, String def);
	Config setString(String key, String value);

	int getInt(String key);
	int getInt(String key, int def);
	Config setInt(String key, int value);

	long getLong(String key);
	long getLong(String key, long def);
	Config setLong(String key, long value);

	Set<String> keys();
}

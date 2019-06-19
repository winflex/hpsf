package io.hpsf.common.config;

import java.util.Set;

/**
 * 
 * @author winflex
 */
public interface IConfig {

	String getString(String key);
	String getString(String key, String def);
	IConfig setString(String key, String value);

	int getInt(String key);
	int getInt(String key, int def);
	IConfig setInt(String key, int value);

	long getLong(String key);
	long getLong(String key, long def);
	IConfig setLong(String key, long value);

	Set<String> keys();
}

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

	short getShort(String key);
	short getShort(String key, short def);
	Config setShort(String key, short value);
	
	int getInt(String key);
	int getInt(String key, int def);
	Config setInt(String key, int value);

	long getLong(String key);
	long getLong(String key, long def);
	Config setLong(String key, long value);
	
	float getFloat(String key);
	float getFloat(String key, float def);
	Config setFloat(String key, float value);
	
	double getDouble(String key);
	double getDouble(String key, double def);
	Config setDouble(String key, double value);
	
	Set<String> keys();
}

package io.hpsf.common.config;

/**
 * 
 * @author winflex
 */
public abstract class AbstractConfig implements IConfig {

	@Override
	public int getInt(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException(key + " not present!");
		}
		return Integer.parseInt(value);
	}

	@Override
	public int getInt(String key, int def) {
		String value = getString(key);
		if (value == null) {
			return def;
		}
		return Integer.parseInt(value);
	}
	
	@Override
	public IConfig setInt(String key, int value) {
		return setString(key, String.valueOf(value));
	}

	@Override
	public long getLong(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException(key + " not present!");
		}
		return Long.parseLong(value);
	}

	@Override
	public long getLong(String key, long def) {
		String value = getString(key);
		if (value == null) {
			return def;
		}
		return Long.parseLong(value);
	}
	
	@Override
	public IConfig setLong(String key, long value) {
		return setString(key, String.valueOf(value));
	}
}

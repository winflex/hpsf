package io.hpsf.common.config;

/**
 * 
 * @author winflex
 */
public abstract class AbstractConfig implements Config {

	@Override
	public short getShort(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException(key + " not present!");
		}
		return Short.parseShort(value);
	}

	@Override
	public short getShort(String key, short def) {
		String value = getString(key);
		if (value == null) {
			return def;
		}
		return Short.parseShort(value);
	}

	@Override
	public Config setShort(String key, short value) {
		return setString(key, String.valueOf(value));
	}

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
	public Config setInt(String key, int value) {
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
	public Config setLong(String key, long value) {
		return setString(key, String.valueOf(value));
	}

	@Override
	public float getFloat(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException(key + " not present!");
		}
		return Float.parseFloat(value);
	}

	@Override
	public float getFloat(String key, float def) {
		String value = getString(key);
		if (value == null) {
			return def;
		}
		return Float.parseFloat(value);
	}

	@Override
	public Config setFloat(String key, float value) {
		return setString(key, String.valueOf(value));
	}
	
	@Override
	public double getDouble(String key) {
		String value = getString(key);
		if (value == null) {
			throw new RuntimeException(key + " not present!");
		}
		return Double.parseDouble(value);
	}
	
	@Override
	public double getDouble(String key, double def) {
		String value = getString(key);
		if (value == null) {
			return def;
		}
		return Double.parseDouble(value);
	}
	
	@Override
	public Config setDouble(String key, double value) {
		return setString(key, String.valueOf(value));
	}
}

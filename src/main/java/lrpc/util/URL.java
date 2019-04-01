package lrpc.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL implements Serializable {

	private static final long serialVersionUID = 1532292376987041921L;

	private static final String UTF_8 = "UTF-8";
	private static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
	private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)");

	private final String protocol;

	private final String username;

	private final String password;

	private final String host;

	private final int port;

	private final String path;

	private final Map<String, String> parameters;

	// ==== cache ====
	private volatile transient String ip;

	public URL(String protocol, String host, int port) {
		this(protocol, null, null, host, port, null, (Map<String, String>) null);
	}

	public URL(String protocol, String host, int port, Map<String, String> parameters) {
		this(protocol, null, null, host, port, null, parameters);
	}

	public URL(String protocol, String host, int port, String path) {
		this(protocol, null, null, host, port, path, (Map<String, String>) null);
	}

	public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
		this(protocol, null, null, host, port, path, parameters);
	}

	public URL(String protocol, String username, String password, String host, int port, String path) {
		this(protocol, username, password, host, port, path, (Map<String, String>) null);
	}

	public URL(String protocol, String username, String password, String host, int port, String path,
			Map<String, String> parameters) {
		if ((username == null || username.length() == 0) && password != null && password.length() > 0) {
			throw new IllegalArgumentException("Invalid url, password without username!");
		}
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = (port < 0 ? 0 : port);
		this.path = path;
		// trim the beginning "/"
		while (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		} else {
			parameters = new HashMap<String, String>(parameters);
		}
		this.parameters = Collections.unmodifiableMap(parameters);
	}

	/**
	 * Parse url string
	 * 
	 * @param url URL string
	 * @return URL instance
	 * @see URL
	 */
	public static URL valueOf(String url) {
		if (url == null || (url = url.trim()).length() == 0) {
			throw new IllegalArgumentException("url == null");
		}
		String protocol = null;
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		String path = null;
		Map<String, String> parameters = null;
		int i = url.indexOf("?"); // seperator between body and parameters
		if (i >= 0) {
			String[] parts = url.substring(i + 1).split("\\&");
			parameters = new HashMap<String, String>();
			for (String part : parts) {
				part = part.trim();
				if (part.length() > 0) {
					int j = part.indexOf('=');
					if (j >= 0) {
						parameters.put(part.substring(0, j), part.substring(j + 1));
					} else {
						parameters.put(part, part);
					}
				}
			}
			url = url.substring(0, i);
		}
		i = url.indexOf("://");
		if (i >= 0) {
			if (i == 0)
				throw new IllegalStateException("url missing protocol: \"" + url + "\"");
			protocol = url.substring(0, i);
			url = url.substring(i + 3);
		} else {
			// case: file:/path/to/file.txt
			i = url.indexOf(":/");
			if (i >= 0) {
				if (i == 0)
					throw new IllegalStateException("url missing protocol: \"" + url + "\"");
				protocol = url.substring(0, i);
				url = url.substring(i + 1);
			}
		}

		i = url.indexOf("/");
		if (i >= 0) {
			path = url.substring(i + 1);
			url = url.substring(0, i);
		}
		i = url.indexOf("@");
		if (i >= 0) {
			username = url.substring(0, i);
			int j = username.indexOf(":");
			if (j >= 0) {
				password = username.substring(j + 1);
				username = username.substring(0, j);
			}
			url = url.substring(i + 1);
		}
		i = url.indexOf(":");
		if (i >= 0 && i < url.length() - 1) {
			port = Integer.parseInt(url.substring(i + 1));
			url = url.substring(0, i);
		}
		if (url.length() > 0)
			host = url;
		return new URL(protocol, username, password, host, port, path, parameters);
	}

	public String getProtocol() {
		return protocol;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getAuthority() {
		if ((username == null || username.length() == 0) && (password == null || password.length() == 0)) {
			return null;
		}
		return (username == null ? "" : username) + ":" + (password == null ? "" : password);
	}

	public String getHost() {
		return host;
	}

	/**
	 * 获取IP地址.
	 * 
	 * 请注意： 如果和Socket的地址对比， 或用地址作为Map的Key查找， 请使用IP而不是Host， 否则配置域名会有问题
	 * 
	 * @return ip
	 */
	public String getIp() {
		if (ip == null) {
			try {
				ip = InetAddress.getByName(host).getHostAddress();
			} catch (UnknownHostException e) {
				ip = host;
			}
		}
		return ip;
	}

	public int getPort() {
		return port;
	}

	public int getPort(int defaultPort) {
		return port <= 0 ? defaultPort : port;
	}

	public String getAddress() {
		return port <= 0 ? host : host + ":" + port;
	}

	public String getPath() {
		return path;
	}

	public String getAbsolutePath() {
		if (path != null && !path.startsWith("/")) {
			return "/" + path;
		}
		return path;
	}

	public URL setProtocol(String protocol) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setUsername(String username) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setPassword(String password) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setAddress(String address) {
		int i = address.lastIndexOf(':');
		String host;
		int port = this.port;
		if (i >= 0) {
			host = address.substring(0, i);
			port = Integer.parseInt(address.substring(i + 1));
		} else {
			host = address;
		}
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setHost(String host) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setPort(int port) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public URL setPath(String path) {
		return new URL(protocol, username, password, host, port, path, getParameters());
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getParameterAndDecoded(String key) {
		return getParameterAndDecoded(key, null);
	}

	public String getParameterAndDecoded(String key, String defaultValue) {
		return decode(getParameter(key, defaultValue));
	}

	public String getParameter(String key) {
		return parameters.get(key);
	}

	public String getParameter(String key, String defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return value;
	}

	public String[] getParameter(String key, String[] defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return COMMA_SPLIT_PATTERN.split(value);
	}

	public double getParameter(String key, double defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Double.parseDouble(value);
	}

	public float getParameter(String key, float defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Float.parseFloat(value);
	}

	public long getParameter(String key, long defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Long.parseLong(value);
	}

	public int getParameter(String key, int defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	public short getParameter(String key, short defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Short.parseShort(value);
	}

	public byte getParameter(String key, byte defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Byte.parseByte(value);
	}

	public float getPositiveParameter(String key, float defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("defaultValue <= 0");
		}
		float value = getParameter(key, defaultValue);
		if (value <= 0) {
			return defaultValue;
		}
		return value;
	}

	public double getPositiveParameter(String key, double defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("defaultValue <= 0");
		}
		double value = getParameter(key, defaultValue);
		if (value <= 0) {
			return defaultValue;
		}
		return value;
	}

	public long getPositiveParameter(String key, long defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("defaultValue <= 0");
		}
		long value = getParameter(key, defaultValue);
		if (value <= 0) {
			return defaultValue;
		}
		return value;
	}

	public int getPositiveParameter(String key, int defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("defaultValue <= 0");
		}
		int value = getParameter(key, defaultValue);
		if (value <= 0) {
			return defaultValue;
		}
		return value;
	}

	public short getPositiveParameter(String key, short defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("defaultValue <= 0");
		}
		short value = getParameter(key, defaultValue);
		if (value <= 0) {
			return defaultValue;
		}
		return value;
	}

	public byte getPositiveParameter(String key, byte defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("defaultValue <= 0");
		}
		byte value = getParameter(key, defaultValue);
		if (value <= 0) {
			return defaultValue;
		}
		return value;
	}

	public char getParameter(String key, char defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return value.charAt(0);
	}

	public boolean getParameter(String key, boolean defaultValue) {
		String value = getParameter(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	public boolean hasParameter(String key) {
		String value = getParameter(key);
		return value != null && value.length() > 0;
	}

	public URL addParameterAndEncoded(String key, String value) {
		if (value == null || value.length() == 0) {
			return this;
		}
		return addParameter(key, encode(value));
	}

	public URL addParameter(String key, boolean value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, char value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, byte value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, short value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, int value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, long value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, float value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, double value) {
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, Enum<?> value) {
		if (value == null)
			return this;
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, Number value) {
		if (value == null)
			return this;
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, CharSequence value) {
		if (value == null || value.length() == 0)
			return this;
		return addParameter(key, String.valueOf(value));
	}

	public URL addParameter(String key, String value) {
		if (key == null || key.length() == 0 || value == null || value.length() == 0) {
			return this;
		}
		// 如果没有修改，直接返回。
		if (value.equals(getParameters().get(key))) { // value != null
			return this;
		}

		Map<String, String> map = new HashMap<String, String>(getParameters());
		map.put(key, value);
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL addParameterIfAbsent(String key, String value) {
		if (key == null || key.length() == 0 || value == null || value.length() == 0) {
			return this;
		}
		if (hasParameter(key)) {
			return this;
		}
		Map<String, String> map = new HashMap<String, String>(getParameters());
		map.put(key, value);
		return new URL(protocol, username, password, host, port, path, map);
	}

	/**
	 * Add parameters to a new url.
	 * 
	 * @param parameters
	 * @return A new URL
	 */
	public URL addParameters(Map<String, String> parameters) {
		if (parameters == null || parameters.size() == 0) {
			return this;
		}

		boolean hasAndEqual = true;
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String value = getParameters().get(entry.getKey());
			if ((value == null && entry.getValue() != null) || (value != null && !value.equals(entry.getValue()))) {
				hasAndEqual = false;
				break;
			}
		}
		// 如果没有修改，直接返回。
		if (hasAndEqual)
			return this;

		Map<String, String> map = new HashMap<String, String>(getParameters());
		map.putAll(parameters);
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL addParametersIfAbsent(Map<String, String> parameters) {
		if (parameters == null || parameters.size() == 0) {
			return this;
		}
		Map<String, String> map = new HashMap<String, String>(parameters);
		map.putAll(getParameters());
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL addParameters(String... pairs) {
		if (pairs == null || pairs.length == 0) {
			return this;
		}
		if (pairs.length % 2 != 0) {
			throw new IllegalArgumentException("Map pairs can not be odd number.");
		}
		Map<String, String> map = new HashMap<String, String>();
		int len = pairs.length / 2;
		for (int i = 0; i < len; i++) {
			map.put(pairs[2 * i], pairs[2 * i + 1]);
		}
		return addParameters(map);
	}

	public URL addParameterString(String query) {
		if (query == null || query.length() == 0) {
			return this;
		}
		return addParameters(parseQueryString(query));
	}

	public URL removeParameter(String key) {
		if (key == null || key.length() == 0) {
			return this;
		}
		return removeParameters(key);
	}

	public URL removeParameters(Collection<String> keys) {
		if (keys == null || keys.size() == 0) {
			return this;
		}
		return removeParameters(keys.toArray(new String[0]));
	}

	public URL removeParameters(String... keys) {
		if (keys == null || keys.length == 0) {
			return this;
		}
		Map<String, String> map = new HashMap<String, String>(getParameters());
		for (String key : keys) {
			map.remove(key);
		}
		if (map.size() == getParameters().size()) {
			return this;
		}
		return new URL(protocol, username, password, host, port, path, map);
	}

	public URL clearParameters() {
		return new URL(protocol, username, password, host, port, path, new HashMap<String, String>());
	}

	public String getRawParameter(String key) {
		if ("protocol".equals(key))
			return protocol;
		if ("username".equals(key))
			return username;
		if ("password".equals(key))
			return password;
		if ("host".equals(key))
			return host;
		if ("port".equals(key))
			return String.valueOf(port);
		if ("path".equals(key))
			return path;
		return getParameter(key);
	}

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>(parameters);
		if (protocol != null)
			map.put("protocol", protocol);
		if (username != null)
			map.put("username", username);
		if (password != null)
			map.put("password", password);
		if (host != null)
			map.put("host", host);
		if (port > 0)
			map.put("port", String.valueOf(port));
		if (path != null)
			map.put("path", path);
		return map;
	}

	@Override
	public String toString() {
		return buildString(false, true);
	}

	public String toFullString() {
		return buildString(true, true);
	}

	public String toFullString(String... parameters) {
		return buildString(true, true, parameters);
	}

	public String toParameterString(String... parameters) {
		StringBuilder buf = new StringBuilder();
		buildParameters(buf, false, parameters);
		return buf.toString();
	}

	private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
		if (getParameters() != null && getParameters().size() > 0) {
			List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
			boolean first = true;
			for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
				if (entry.getKey() != null && entry.getKey().length() > 0
						&& (includes == null || includes.contains(entry.getKey()))) {
					if (first) {
						if (concat) {
							buf.append("?");
						}
						first = false;
					} else {
						buf.append("&");
					}
					buf.append(entry.getKey());
					buf.append("=");
					buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
				}
			}
		}
	}

	private String buildString(boolean appendUser, boolean appendParameter, String... parameters) {
		return buildString(appendUser, appendParameter, false, parameters);
	}

	private String buildString(boolean appendUser, boolean appendParameter, boolean useIP, String... parameters) {
		StringBuilder buf = new StringBuilder();
		if (protocol != null && protocol.length() > 0) {
			buf.append(protocol);
			buf.append("://");
		}
		if (appendUser && username != null && username.length() > 0) {
			buf.append(username);
			if (password != null && password.length() > 0) {
				buf.append(":");
				buf.append(password);
			}
			buf.append("@");
		}
		String host;
		if (useIP) {
			host = getIp();
		} else {
			host = getHost();
		}
		if (host != null && host.length() > 0) {
			buf.append(host);
			if (port > 0) {
				buf.append(":");
				buf.append(port);
			}
		}
		String path = getPath();
		if (path != null && path.length() > 0) {
			buf.append("/");
			buf.append(path);
		}
		if (appendParameter) {
			buildParameters(buf, true, parameters);
		}
		return buf.toString();
	}

	public java.net.URL toJavaURL() {
		try {
			return new java.net.URL(toString());
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(host, port);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + port;
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URL other = (URL) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/**
	 * parse query string to Parameters.
	 * 
	 * @param qs query string.
	 * @return Parameters instance.
	 */
	private Map<String, String> parseQueryString(String qs) {
		if (qs == null || qs.length() == 0)
			return new HashMap<String, String>();
		return parseKeyValuePair(qs, "\\&");
	}

	/**
	 * parse key-value pair.
	 * 
	 * @param str           string.
	 * @param itemSeparator item separator.
	 * @return key-value map;
	 */
	private Map<String, String> parseKeyValuePair(String str, String itemSeparator) {
		String[] tmp = str.split(itemSeparator);
		Map<String, String> map = new HashMap<String, String>(tmp.length);
		for (int i = 0; i < tmp.length; i++) {
			Matcher matcher = KVP_PATTERN.matcher(tmp[i]);
			if (matcher.matches() == false)
				continue;
			map.put(matcher.group(1), matcher.group(2));
		}
		return map;
	}

	private String encode(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		try {
			return URLEncoder.encode(value, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String decode(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		try {
			return URLDecoder.decode(value, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
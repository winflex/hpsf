/**
 * 
 */
package lrpc.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * http://username:password@192.168.1.10:8080/api?param1=value1&param2=value2
 * 
 * 
 * @see org.apache.dubbo.common.URL
 * @author winflex
 */
public class URL implements Serializable {

	private static final long serialVersionUID = 2148704483142134599L;

	private final String protocol;

	private final String username;

	private final String password;

	private final String host;

	private final int port;

	private final String path;

	private final Map<String, String> parameters;

	private String toStringCache;

	public URL(String protocol, String username, String password, String host, int port, String path,
			Map<String, String> parameters) {
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.path = path;
		if (parameters == null) {
			parameters = new HashMap<>();
		} else {
			parameters = new HashMap<>(parameters);
		}
		this.parameters = Collections.unmodifiableMap(parameters);
	}

	public static URL valueOf(String url) {
		if (url == null || (url = url.trim()).length() == 0) {
			throw new MalformedURLException("url == null");
		}

		String protocol = null;
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		String path = null;
		Map<String, String> parameters = null;

		int i = url.indexOf("?"); // separator between body and parameters
		if (i >= 0) {
			String[] parts = url.substring(i + 1).split("&");
			parameters = new HashMap<>();
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
			if (i == 0) {
				throw new MalformedURLException("url missing protocol: \"" + url + "\"");
			}
			protocol = url.substring(0, i);
			url = url.substring(i + 3);
		} else {
			// case: file:/path/to/file.txt
			i = url.indexOf(":/");
			if (i >= 0) {
				if (i == 0) {
					throw new MalformedURLException("url missing protocol: \"" + url + "\"");
				}
				protocol = url.substring(0, i);
				url = url.substring(i + 1);
			}
		}

		i = url.indexOf("/");
		if (i >= 0) {
			path = url.substring(i + 1);
			url = url.substring(0, i);
		}
		i = url.lastIndexOf("@");
		if (i >= 0) {
			username = url.substring(0, i);
			int j = username.indexOf(":");
			if (j >= 0) {
				password = username.substring(j + 1);
				username = username.substring(0, j);
			}
			url = url.substring(i + 1);
		}
		i = url.lastIndexOf(":");
		if (i >= 0 && i < url.length() - 1) {
			if (url.lastIndexOf("%") > i) {
				// ipv6 address with scope id
				// e.g. fe80:0:0:0:894:aeec:f37d:23e1%en0
				// see https://howdoesinternetwork.com/2013/ipv6-zone-id
				// ignore
			} else {
				port = Integer.parseInt(url.substring(i + 1));
				url = url.substring(0, i);
			}
		}
		if (url.length() > 0) {
			host = url;
		}
		return new URL(protocol, username, password, host, port, path, parameters);
	}

	public String getToStringCache() {
		return toStringCache;
	}

	public void setToStringCache(String toStringCache) {
		this.toStringCache = toStringCache;
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

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public static class MalformedURLException extends RuntimeException {

		private static final long serialVersionUID = -2120730556996852496L;

		public MalformedURLException() {
			super();
		}

		public MalformedURLException(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public MalformedURLException(String message, Throwable cause) {
			super(message, cause);
		}

		public MalformedURLException(String message) {
			super(message);
		}

		public MalformedURLException(Throwable cause) {
			super(cause);
		}
	}
}

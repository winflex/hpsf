/**
 * 
 */
package lrpc.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 *
 * @author winflex
 */
public class URLTest {
	
	@Test
	public void testValueOf() {
 		Map<String, String> parameters = new HashMap<>();
 		parameters.put("param1", "value1");
 		parameters.put("param2", "value2");
		URL url = URL.valueOf("http://root:root@localhost:8080/api?param1=value1&param2=value2");
		assertEquals(url.getProtocol(), "http");
		assertEquals(url.getUsername(), "root");
		assertEquals(url.getPassword(), "root");
		assertEquals(url.getHost(), "localhost");
		assertEquals(url.getPort(), 8080);
		assertEquals(url.getPath(), "api");
		assertEquals(url.getParameters(), parameters);
	}
	
}

package io.hpsf.sample;

import org.apache.curator.test.TestingServer;

import lombok.extern.slf4j.Slf4j;

/**
 * java MockZookeeperServer [2181]
 * 
 * @author winflex
 */
@Slf4j
public class MockZookeeperServer {
	
	public static void main(String[] args) throws Exception {
		int port = 2181;
		if (args.length> 0) {
			port = Integer.parseInt(args[0]);
		}
		
		TestingServer zk = new TestingServer(port);
		log.info("zookeeper is listening on port " + port);
		System.in.read();
		zk.close();
	}
}

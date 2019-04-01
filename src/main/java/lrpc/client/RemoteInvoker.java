package lrpc.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;
import lrpc.common.IInvoker;
import lrpc.common.Invocation;
import lrpc.common.RpcException;
import lrpc.util.Endpoint;
import lrpc.util.concurrent.IFuture;

/**
 * 
 * @author winflex
 */
public class RemoteInvoker<T> implements IInvoker<T> {

	private static final Logger logger = LoggerFactory.getLogger(RemoteInvoker.class);

	private final Endpoint endpoint;
	private final Class<T> iface;
	private int connectionCount;
	private long connectTimeoutMillis;
	private long invokeTimeoutMillis;
	private final EventLoopGroup workerGroup;

	private final List<RpcConnection> connections = new ArrayList<>();
	private final AtomicInteger connectionIndex = new AtomicInteger(0);

	public RemoteInvoker(Endpoint endpoint, Class<T> iface, int connectionCount, long connectTimeoutMillis,
			long invokeTimeoutMillis, EventLoopGroup workerGroup) throws RpcException {
		this.endpoint = endpoint;
		this.iface = iface;
		this.connectionCount = connectionCount;
		this.connectTimeoutMillis = connectTimeoutMillis;
		this.invokeTimeoutMillis = invokeTimeoutMillis;
		this.workerGroup = workerGroup;

		try {
			for (int i = 0; i < connectionCount; i++) {
				RpcConnection conn = new DefaultConnection(endpoint, workerGroup, connectTimeoutMillis);
				connections.add(conn);
			}
		} catch (IOException e) {
			closeConnections();
			throw new RpcException(e);
		}
	}

	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		RpcConnection conn = selectConnection();
		IFuture<?> future = conn.send(invocation);
		if (invokeTimeoutMillis > 0) {
			return future.get(invokeTimeoutMillis, TimeUnit.MILLISECONDS);
		} else {
			return future.get();
		}
	}

	private RpcConnection selectConnection() {
		RpcConnection conn;
		if (connections.size() == 1) {
			conn = connections.get(0);
		} else {
			conn = connections.get(Math.abs(connectionIndex.getAndIncrement() % connections.size()));
		}
		return conn;
	}

	@Override
	public Class<T> getInterface() {
		return iface;
	}

	@Override
	public synchronized void close() throws Exception {
		closeConnections();
	}

	private void closeConnections() {
		for (RpcConnection conn : connections) {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		connections.clear();
	}
}

package lrpc.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lrpc.util.concurrent.DefaultPromise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseFuture extends DefaultPromise<Object> {

	private static final long serialVersionUID = 275517284412500195L;

	private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);
	
	private static final ConcurrentMap<Long, ResponseFuture> inflightFutures = new ConcurrentHashMap<>();
	
	public static void doneWithResult(long requestId, Object result) {
		ResponseFuture future = inflightFutures.get(requestId);
		if (future == null) {
			logger.warn("No future correlate with " + requestId);
			return;
		}
		
		future.setSuccess(result);
	}
	
	public static void doneWithException(long requestId, Throwable cause) {
		ResponseFuture future = inflightFutures.get(requestId);
		if (future == null) {
			logger.warn("No future correlate with " + requestId);
			return;
		}
		future.setFailure(cause);
	}
	
	private final long requestId;


	public ResponseFuture(long requestId) {
		this.requestId = requestId;
		inflightFutures.put(requestId, this);
	}

	public final long getRequestId() {
		return requestId;
	}
	
}

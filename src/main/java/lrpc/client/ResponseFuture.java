package lrpc.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lrpc.util.concurrent.DefaultPromise;
import lrpc.util.concurrent.NamedThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author winflex
 */
public class ResponseFuture extends DefaultPromise<Object> {

	private static final long serialVersionUID = 275517284412500195L;

	private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

	private static final ConcurrentMap<Long, ResponseFuture> inflightFutures = new ConcurrentHashMap<>();

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
			new NamedThreadFactory("ResponseFuture-Watchdog", true));

	public static void doneWithResult(long requestId, Object result) {
		ResponseFuture future = inflightFutures.get(requestId);
		if (future == null) {
			logger.warn("No future correlate with " + requestId + ", maybe it's timed out");
			return;
		}
		future.cancelTimeoutTask();
		future.setSuccess(result);
	}

	public static void doneWithException(long requestId, Throwable cause) {
		ResponseFuture future = inflightFutures.get(requestId);
		if (future == null) {
			logger.warn("No future correlate with " + requestId + ", maybe it's timed out");
			return;
		}
		future.cancelTimeoutTask();
		future.setFailure(cause);
	}

	private final long futureId;

	private final ScheduledFuture<?> timeoutFuture;

	public ResponseFuture(long futureId, int timeoutMillis) {
		if (timeoutMillis <= 0) {
			throw new IllegalArgumentException("timeoutMillis must be positive");
		}
		this.futureId = futureId;
		inflightFutures.put(futureId, this);
		this.timeoutFuture = scheduler.schedule(() -> {
			setFailure(new TimeoutException("timed out after " + timeoutMillis + "ms"));
		}, timeoutMillis, TimeUnit.MILLISECONDS);
	}

	private void cancelTimeoutTask() {
		timeoutFuture.cancel(true);
	}

	public final long getRequestId() {
		return futureId;
	}
}

package io.hpsf.rpc.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.hpsf.common.concurrent.DefaultPromise;
import io.hpsf.common.concurrent.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author winflex
 */
@Slf4j
public class ResponseFuture extends DefaultPromise<Object> {

	private static final long serialVersionUID = 275517284412500195L;

	private static final ConcurrentMap<Long, ResponseFuture> inflightFutures = new ConcurrentHashMap<>();

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
			new NamedThreadFactory("ResponseFuture-Watchdog", true));

	public static void doneWithResult(long requestId, Object result) {
		ResponseFuture future = inflightFutures.get(requestId);
		if (future == null) {
			log.warn("No future correlate with " + requestId + ", maybe it's timed out");
			return;
		}
		future.cancelTimeoutTask();
		future.setSuccess(result);
	}

	public static void doneWithException(long requestId, Throwable cause) {
		ResponseFuture future = inflightFutures.get(requestId);
		if (future == null) {
			log.warn("No future correlate with " + requestId + ", maybe it's timed out");
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

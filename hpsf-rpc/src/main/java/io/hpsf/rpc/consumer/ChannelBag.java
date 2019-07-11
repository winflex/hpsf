package io.hpsf.rpc.consumer;

import static io.hpsf.common.util.TimeUtils.currentTime;
import static io.hpsf.common.util.TimeUtils.elapsedMillis;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import io.hpsf.common.concurrent.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接池(与传统数据库连接池概念不同, 这里的连接池中的连接是可以被多个线程同时使用的)
 * 
 * 
 * @author winflex
 */
@Slf4j
public class ChannelBag {

	private static final ScheduledExecutorService houseKeepingExecutor = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("hpsf-housekeeper", true));

	private final Bootstrap bootstrap;
	private final int channelCount;
	private final HealthChecker healthChecker;

	private final CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
	private final AtomicInteger selectIndex = new AtomicInteger();

	public ChannelBag(Bootstrap bootstrap, int channelCount, HealthChecker healthChecker) throws IOException {
		if (channelCount <= 0) {
			throw new IllegalArgumentException("maxConnections must be positive");
		}
		this.channelCount = channelCount;
		this.bootstrap = bootstrap;
		this.healthChecker = healthChecker;
		houseKeepingExecutor.scheduleWithFixedDelay(() -> keepHouse(), 100, 100, TimeUnit.MILLISECONDS);
	}

	public Channel getChannel(int timeoutMillis) throws TimeoutException {
		final AtomicInteger selectIndex = this.selectIndex;
		final long start = currentTime();
		int size;
		for (; elapsedMillis(start) < timeoutMillis;) {
			if ((size = channels.size()) > 0) {
				try {
					Channel ch = channels.get(selectIndex.getAndIncrement() & size);
					if (healthChecker.isHealthy(ch)) {
						return ch;
					} else {
						channels.remove(ch);
					}
				} catch (IndexOutOfBoundsException e) {
					// It's ok, let's try it again
				}
			} else {
				LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
			}
		}

		throw new TimeoutException("get channel timed out after " + elapsedMillis(start));
	}

	private void keepHouse() {
		// 清除不可用连接
		for (Channel ch : channels) {
			if (!healthChecker.isHealthy(ch)) {
				channels.remove(ch);
			}
		}
		// 维持指定连接数
		int count = this.channelCount - channels.size();
		for (int i = 0; i < count; i++) {
			bootstrap.connect().addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						// TODO 优化:这个add操作可以让housekeeper线程来做
						channels.add(future.channel());
					} else {
						log.error(future.cause().getMessage(), future.cause());
					}
				}
			});
		}
	}

	public void close() {
		channels.forEach(ch -> ch.close());
	}

	public static interface HealthChecker {

		public static final HealthChecker ACTIVE = (channel) -> channel.isActive();

		boolean isHealthy(Channel channel);
	}
}

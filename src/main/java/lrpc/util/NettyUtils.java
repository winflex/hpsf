package lrpc.util;

import lrpc.common.protocol.RpcMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(NettyUtils.class);
	
	public static final ChannelFutureListener li = new ChannelFutureListener() {
		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (!future.isSuccess()) {
				logger.error("write message failed", future.cause());
			}
		}
	};
	
	public static ChannelFuture writeAndFlush(Channel ch, RpcMessage msg) {
		// TODO check for ch.isWritable();
		return ch.writeAndFlush(msg).addListener(li);
	}
	
}

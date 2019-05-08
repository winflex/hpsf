package lrpc.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import lrpc.common.protocol.RpcMessage;

@Slf4j
public class NettyUtils {
	
	public static final ChannelFutureListener li = new ChannelFutureListener() {
		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (!future.isSuccess()) {
				log.error("write message failed", future.cause());
			}
		}
	};
	
	public static ChannelFuture writeAndFlush(Channel ch, RpcMessage<?> msg) {
		// TODO check for ch.isWritable();
		return ch.writeAndFlush(msg).addListener(li);
	}
	
}

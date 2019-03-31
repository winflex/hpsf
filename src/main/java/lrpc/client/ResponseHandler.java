package lrpc.client;

import static lrpc.common.protocol.RpcMessage.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lrpc.common.RpcResult;
import lrpc.common.protocol.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 *
 * @author winflex
 */
public class ResponseHandler extends SimpleChannelInboundHandler<RpcMessage> {

	private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcMessage resp) throws Exception {
		switch (resp.getType()) {
		case TYPE_HEARTBEAT_RESPONSE:
			// nothing to do
			break;
		case TYPE_INVOKE_RESPONSE:
			long requestId = resp.getId();
			RpcResult result = (RpcResult) resp.getData();
			if (result.isSuccess()) {
				ResponseFuture.doneWithResult(requestId, result.getResult());
			} else {
				ResponseFuture.doneWithException(requestId, result.getCause());
			}
			break;
		default:
			logger.warn("Unexpected message recieved, type = " + resp.getType());
			break;
		}
	}
}

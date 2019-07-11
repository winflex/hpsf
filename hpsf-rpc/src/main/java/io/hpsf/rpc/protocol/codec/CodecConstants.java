package io.hpsf.rpc.protocol.codec;

/**
 * 
 * @author winflex
 */
public interface CodecConstants {
	
	int HEADER_LENGTH = 15;
	
	int BODY_LENGTH_OFFSET = 11;
	
	short MAGIC = (short) 0xebab;
}

package lrpc.common.codec;

/**
 * 
 * @author winflex
 */
public interface CodecConstants {
	
	int HEADER_LENGTH = 16;
	
	int BODY_LENGTH_OFFSET = 11;
	
	short MAGIC = (short) 0xebab;
}

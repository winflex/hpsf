package lrpc.common.serialize;

import java.io.IOException;

/**
 * 
 * @author winflex
 */
public interface ISerializer {
	
	byte[] serialize(Object obj) throws IOException;
	
	<T> T deserialize(byte[] serializedData) throws IOException, ClassNotFoundException;
}

package io.hpsf.serialization.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 序列化/反序列化接口定义
 * 
 * @author winflex
 */
public interface Serializer {

	void serialize(Object obj, OutputStream out) throws SerializeException;

	<T> T deserialize(InputStream in) throws SerializeException;
}

package io.hpsf.serialization.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;

import io.hpsf.serialization.api.Serializer;

/**
 * 
 * @author winflex
 */
public class HessianSerializer implements Serializer {

	@Override
	public void serialize(Object obj, OutputStream out) throws IOException {
		Hessian2Output ho = new Hessian2Output(out);
		ho.setSerializerFactory(SERIALIZER_FACTORY);
		ho.writeObject(obj);
		ho.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream in) throws IOException, ClassNotFoundException {
		Hessian2Input hi = new Hessian2Input(in);
		hi.setSerializerFactory(SERIALIZER_FACTORY);
		return (T) hi.readObject();
	}

	private static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory() {
		@Override
		public ClassLoader getClassLoader() {
			return Thread.currentThread().getContextClassLoader();
		}
	};
}

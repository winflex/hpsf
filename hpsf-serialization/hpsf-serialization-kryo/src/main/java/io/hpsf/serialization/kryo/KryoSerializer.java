package io.hpsf.serialization.kryo;

import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.hpsf.serialization.api.SerializeException;
import io.hpsf.serialization.api.Serializer;

/**
 * TODO 解决kryo序列化没有无参构造方法异常的问题
 * @author winflex
 */
public class KryoSerializer implements Serializer {
	
	public static final KryoSerializer INSTANCE = new KryoSerializer();

	private final static ThreadLocal<Kryo> localKryo = new ThreadLocal<Kryo>() {
		protected final Kryo initialValue() {
			return new Kryo();
		}
	};

	@Override
	public final void serialize(Object obj, OutputStream out) throws SerializeException {
		localKryo.get().writeClassAndObject(new Output(out), obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> T deserialize(InputStream in) throws SerializeException {
		return (T) localKryo.get().readClassAndObject(new Input(in));
	}
}

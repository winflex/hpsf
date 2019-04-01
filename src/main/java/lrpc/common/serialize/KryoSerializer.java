package lrpc.common.serialize;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 
 * @author winflex
 */
public class KryoSerializer implements ISerializer {
	
	public static final KryoSerializer INSTANCE = new KryoSerializer();

	private final static ThreadLocal<Kryo> localKryo = new ThreadLocal<Kryo>() {
		protected final Kryo initialValue() {
			return new Kryo();
		}
	};

	private final static ThreadLocal<Output> localOutput = new ThreadLocal<Output>() {
		protected final Output initialValue() {
			return new Output(4 * 1024, 256 * 1024);
		}
	};

	@Override
	public final byte[] serialize(Object obj) throws IOException {
		Output output = localOutput.get();
		try {
			localKryo.get().writeClassAndObject(output, obj);
			return output.toBytes();
		} finally {
			output.setPosition(0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> T deserialize(byte[] serializedData) throws IOException, ClassNotFoundException {
		Input input = new Input(serializedData);
		return (T) localKryo.get().readClassAndObject(input);
	}
}

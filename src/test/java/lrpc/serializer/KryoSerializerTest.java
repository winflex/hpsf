package lrpc.serializer;

import java.io.IOException;
import java.io.Serializable;
import static org.junit.Assert.*;

import lrpc.common.serialize.KryoSerializer;

import org.junit.Test;

public class KryoSerializerTest {
	
	@Test
	public void testSerializer() throws IOException, ClassNotFoundException {
		KryoSerializer kryo = KryoSerializer.INSTANCE;
		TestBean bean = new TestBean();
		bean.setField1("field1");
		
		byte[] stream = kryo.serialize(bean);
		TestBean newBean = kryo.deserialize(stream);
		assertSame(bean.getClass(), newBean.getClass());
		assertEquals(bean.getField1(), newBean.getField1());
	}
	
	public static final class TestBean implements Serializable {
		private static final long serialVersionUID = -4329933990125917653L;
		private String field1;
		

		public String getField1() {
			return field1;
		}

		public void setField1(String field1) {
			this.field1 = field1;
		}
	}
}

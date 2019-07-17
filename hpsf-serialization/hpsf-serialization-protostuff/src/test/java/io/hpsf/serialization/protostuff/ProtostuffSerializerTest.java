package io.hpsf.serialization.protostuff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.hpsf.serialization.api.SerializeException;

/**
 * 
 * @author winflex
 */
public class ProtostuffSerializerTest {

	@Test
	public void testBean() throws SerializeException {
		Bean bean = new Bean();
		bean.setField1("xxx");

		Map<String, String> map = new HashMap<>();
		map.put("xx", "xx");
		bean.setField2(map);

		List<String> list = new ArrayList<>();
		list.add("xx");
		bean.setField3(list);

		ProtostuffSerializer s = new ProtostuffSerializer();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.serialize(bean, baos);

		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Bean decoded = s.deserialize(bais);

		Assert.assertEquals(bean, decoded);

	}

	@Test
	public void testMap() throws SerializeException {
		Map<String, String> map = new HashMap<>();
		map.put("xx", "xx");

		ProtostuffSerializer s = new ProtostuffSerializer();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		s.serialize(map, baos);

		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Map<String, String> decoded = s.deserialize(bais);

		Assert.assertEquals(map, decoded);
	}
}

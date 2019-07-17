package io.hpsf.sample.benchmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.hpsf.common.util.ExceptionUtils;
import io.hpsf.serialization.api.Serializer;
import io.hpsf.serialization.hessian.HessianSerializer;
import io.hpsf.serialization.protostuff.ProtostuffSerializer;
import lombok.Data;

/**
 * 
 * @author winflex
 */
public class SerializationBenchMark {

	public static void main(String[] args) {
		final Bean bean = newBean(true);

		LoadRunner lr = LoadRunner.builder().threads(1).millis(30000).transaction(() -> proto(bean)).build();
		lr.start();
	}

	static final Serializer hessian = new HessianSerializer();
	static void hessian(Bean bean) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			hessian.serialize(bean, baos);
			hessian.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		} catch (Exception e) {
			ExceptionUtils.throwException(e);
		}
	}
	
	static final Serializer proto = new ProtostuffSerializer();
	static void proto(Bean bean) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			proto.serialize(bean, baos);
			proto.deserialize(new ByteArrayInputStream(baos.toByteArray()));
		} catch (Exception e) {
			ExceptionUtils.throwException(e);
		}
	}
	
	static Bean newBean(boolean setChlid) {
		Bean bean = new Bean();

		bean.setField1("xx");
		bean.setField2(1);

		Map<String, String> map = new HashMap<>();
		map.put("xxx", "xxxxx");
		bean.setField3(map);

		List<String> list = new ArrayList<>();
		list.add("xxx");
		bean.setField4(list);

		if (setChlid)
			bean.setField5(newBean(false));

		return bean;
	}

	@Data
	public static class Bean implements Serializable {

		private static final long serialVersionUID = 3481455535785804890L;

		private String field1;
		private int field2;
		private Map<String, String> field3;
		private List<String> field4;
		private Bean field5;
	}
}

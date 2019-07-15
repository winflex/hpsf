package io.hpsf.sample.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.hpsf.rpc.consumer.GenericService;
import io.hpsf.sample.api.AddService;

/**
 * 
 * @author winflex
 */
public class SpringConsumer {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-consumer.xml");
		AddService service = (AddService) ctx.getBean("addService");
		int result = service.add(1, 2);
		System.out.println("invoke result: " + result);

		GenericService genericService = (GenericService) ctx.getBean("genericService");
		int genericResult = (int) genericService.$invoke("add", new Class<?>[] { int.class, int.class }, new Object[] { 1, 2 });
		System.out.println("genertic invoke result: " + genericResult);
		
		ctx.close();
	}
}

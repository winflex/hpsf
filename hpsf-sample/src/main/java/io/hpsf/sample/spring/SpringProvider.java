package io.hpsf.sample.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author winflex
 */
public class SpringProvider {
	
	public static void main(String[] args) throws Throwable {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-provider.xml");
		System.in.read();
		ctx.close();
	}
	
}

package io.hpsf.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import io.hpsf.spring.bean.parser.HpsfClientBeanParser;
import io.hpsf.spring.bean.parser.HpsfReferenceBeanParser;
import io.hpsf.spring.bean.parser.HpsfServerBeanParser;
import io.hpsf.spring.bean.parser.HpsfServiceBeanParser;

/**
 * 定义xml标签与bean解析器的映射关系
 * 
 * @author winflex
 */
public class HpsfNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("server", new HpsfServerBeanParser());
		registerBeanDefinitionParser("service", new HpsfServiceBeanParser());
		registerBeanDefinitionParser("client", new HpsfClientBeanParser());
		registerBeanDefinitionParser("reference", new HpsfReferenceBeanParser());
	}
}

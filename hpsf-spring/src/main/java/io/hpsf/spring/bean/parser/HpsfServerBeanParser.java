package io.hpsf.spring.bean.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import io.hpsf.spring.bean.HpsfServerBean;

/**
 * 
 * @author winflex
 */
public class HpsfServerBeanParser extends AbstractBeanParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition def = new RootBeanDefinition();
		def.setBeanClass(HpsfServerBean.class);
		setBeanProperty(def, element, "ip", false);
		setBeanProperty(def, element, "port", false);
		setBeanProperty(def, element, "registry", true);
		setBeanProperty(def, element, "ioThreads", false);
		setBeanProperty(def, element, "heartbeatInterval", false);
		setBeanProperty(def, element, "serializer", false);
		return registerBean(def, element, parserContext);
	}
}

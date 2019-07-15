package io.hpsf.spring.bean.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import io.hpsf.spring.bean.HpsfClientBean;

/**
 * 
 * @author winflex
 */
public class HpsfClientBeanParser extends AbstractBeanParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition def = new RootBeanDefinition();
		def.setBeanClass(HpsfClientBean.class);
		setBeanProperty(def, element, "registry", true);
		setBeanProperty(def, element, "ioThreads", false);
		setBeanProperty(def, element, "connectTimeoutMillis", false);
		setBeanProperty(def, element, "requestTimeoutMillis", false);
		setBeanProperty(def, element, "serializer", false);
		setBeanProperty(def, element, "maxConnectionPerServer", false);
		return registerBean(def, element, parserContext);
	}

}

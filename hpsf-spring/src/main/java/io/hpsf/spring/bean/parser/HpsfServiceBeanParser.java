package io.hpsf.spring.bean.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import io.hpsf.spring.bean.HpsfServerBean;
import io.hpsf.spring.bean.HpsfServiceBean;

/**
 * 
 * @author winflex
 */
public class HpsfServiceBeanParser extends AbstractBeanParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition def = new RootBeanDefinition();
		def.setBeanClass(HpsfServiceBean.class);
		setBeanProperty(def, element, "id", false);
		setBeanRefProperty(def, element, "ref", true);
		setBeanProperty(def, element, "version", true);
		// 如果没有配server, 那么默认其为HpsfServerBean.class.getSimpleName()
		setBeanRefPropertyWithDefault(def, element, "server", HpsfServerBean.class.getSimpleName());
		setBeanProperty(def, element, "coreThreads", false);
		setBeanProperty(def, element, "maxThreads", false);
		setBeanProperty(def, element, "interface", false);
		return registerBean(def, element, parserContext);
	}
}

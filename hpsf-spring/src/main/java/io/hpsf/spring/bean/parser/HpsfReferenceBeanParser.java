package io.hpsf.spring.bean.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import io.hpsf.spring.bean.HpsfClientBean;
import io.hpsf.spring.bean.HpsfReferenceBean;

/**
 * 
 * @author winflex
 */
public class HpsfReferenceBeanParser extends AbstractBeanParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition def = new RootBeanDefinition();
		def.setBeanClass(HpsfReferenceBean.class);
		setBeanProperty(def, element, "interface", true);
		setBeanProperty(def, element, "version", true);
		// 如果没有配client, 那么默认其为HpsfClientBean.class.getSimpleName()
		setBeanRefPropertyWithDefault(def, element, "client", HpsfClientBean.class.getSimpleName());
		setBeanProperty(def, element, "generic", false);
		return registerBean(def, element, parserContext);
	}

}

package io.hpsf.spring.bean.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
		setBeanProperty(def, element, "interface", false);
		
		NodeList childs = element.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				if (child.getLocalName().equals("threadPool")) { // thread pool 
					setBeanProperty(def, childElement, "corePoolSize", false);
					setBeanProperty(def, childElement, "maxPoolSize", false);
					setBeanProperty(def, childElement, "queueSize", false);
					setBeanProperty(def, childElement, "threadName", false);
					setBeanProperty(def, childElement, "keepAliveTime", false);
					setBeanProperty(def, childElement, "allowCoreThreadTimeout", false);
				} 
			}
		}
		return registerBean(def, element, parserContext);
	}
}

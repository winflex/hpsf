package io.hpsf.spring.bean.parser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Bean解析器
 * 
 * @author winflex
 */
public abstract class AbstractBeanParser implements BeanDefinitionParser {

	protected BeanDefinition registerBean(RootBeanDefinition definition, Element element, ParserContext parserContext) {
		String id = element.getAttribute("id");
		if (id == null || id.isEmpty()) {
			// 没有配id统一默认为类名
			id = definition.getBeanClass().getSimpleName();
		}
		if (parserContext.getRegistry().containsBeanDefinition(id)) {
			throw new IllegalStateException("Duplicate bean id: " + id);
		}

		BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

		return definition;
	}

	protected void setBeanProperty(RootBeanDefinition definition, Element element, String propertyName,
			boolean required) {
		String value = element.getAttribute(propertyName);
		if (required) {
			if (value == null || value.isEmpty()) {
				throw new BeanDefinitionValidationException("Attribute [" + propertyName + "] is required.");
			}
		}
		if (value != null && !value.isEmpty()) {
			definition.getPropertyValues().addPropertyValue(propertyName, value);
		}
	}

	protected void setBeanRefProperty(RootBeanDefinition definition, Element element, String propertyName,
			boolean required) {
		String ref = element.getAttribute(propertyName);
		if (required) {
			if (ref == null || ref.isEmpty()) {
				throw new BeanDefinitionValidationException("Attribute [" + propertyName + "] is required.");
			}
		}
		if (ref != null && !ref.isEmpty()) {
			definition.getPropertyValues().addPropertyValue(propertyName, new RuntimeBeanReference(ref));
		}
	}

	protected void setBeanRefPropertyWithDefault(RootBeanDefinition definition, Element element, String propertyName,
			String def) {
		String ref = element.getAttribute(propertyName);
		if (ref == null || ref.isEmpty()) {
			ref = def;
		}
		if (ref != null && !ref.isEmpty()) {
			definition.getPropertyValues().addPropertyValue(propertyName, new RuntimeBeanReference(ref));
		}
	}
}

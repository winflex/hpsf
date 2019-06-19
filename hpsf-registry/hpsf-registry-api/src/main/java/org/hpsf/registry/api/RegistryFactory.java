package org.hpsf.registry.api;

import io.hpsf.common.ExtensionLoader;

/**
 * 
 * @author winflex
 */
public class RegistryFactory {

	public IRegistry create(RegistryConfig config) throws Exception {
		String extName = config.getString("name");
		if (extName == null) {
			extName = "default";
		}
		return ExtensionLoader.getLoader(IRegistry.class).getExtension(extName);
	}
}

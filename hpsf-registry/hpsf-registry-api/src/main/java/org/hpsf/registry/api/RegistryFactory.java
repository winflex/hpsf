package org.hpsf.registry.api;

import io.hpsf.common.ExtensionLoader;

/**
 * 
 * @author winflex
 */
public class RegistryFactory {

	public Registry create(RegistryConfig config) throws Exception {
		String extName = config.getString("name");
		if (extName == null) {
			extName = "default";
		}
		return ExtensionLoader.getLoader(Registry.class).getExtension(extName);
	}
}

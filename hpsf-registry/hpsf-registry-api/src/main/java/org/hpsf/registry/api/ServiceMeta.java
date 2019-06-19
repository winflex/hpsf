package org.hpsf.registry.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author winflex
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMeta {
	
	private String serviceName;
	
	private String serviceVersion;
	
	
	public final String directoryString() {
		return serviceName + "-" + serviceVersion;
	}
}

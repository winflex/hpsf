package org.hpsf.registry.api;

import io.hpsf.common.Endpoint;
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
public class Registration {
	
	private Endpoint endpoint;
	
	private ServiceMeta serviceMeta;
	
	
}

package org.hpsf.registry.api;

import javax.xml.ws.Endpoint;

import lombok.Data;

/**
 * 
 * @author winflex
 */
@Data
public class Registration {
	
	private Endpoint endpoint;
	
	private ServiceMeta serviceMeta;
	
}

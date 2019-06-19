package io.hpsf.rpc.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 
 * 
 * @author winflex
 */
@Data
public class Invocation implements Serializable {

    private static final long serialVersionUID = -806213717009304249L;

    private String serviceName;
    
    private String serviceVersion;
    
    private String className;
    
    private String methodName;
	
    private Class<?>[] parameterTypes;

    private Object[] paremeters;
    
    private Map<String, String> attachments = new HashMap<String, String>();
    
    public String getAttachment(String key) {
    	return attachments.get(key);
    }

}
package io.hpsf.rpc;

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

    private String className; // 同时也是服务名
    
    private String version;
    
    private String methodName;
	
    private Class<?>[] parameterTypes;

    private Object[] paremeters;
    
    private Map<String, String> attachments = new HashMap<String, String>();
    
    public String getAttachment(String key) {
    	return attachments.get(key);
    }

}
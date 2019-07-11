package io.hpsf.rpc.provider;

import java.util.concurrent.Executor;

import lombok.Data;

/**
 * 发布服务信息
 * 
 * @author winflex
 */
@Data
public class Publishment {
	
	private String serviceName;
	
	private String serviceVersion;
	
	private Object serviceInstance;
	
	private Executor executor;
	
}

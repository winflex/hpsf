package io.hpsf.rpc.provider;

import java.util.concurrent.Executor;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 发布服务信息
 * 
 * @author winflex
 */
@Data
@AllArgsConstructor
class Publishment {
	
	private String serviceName; // 必须是服务接口类型
	
	private String serviceVersion;
	
	private Object serviceInstance;
	
	private Executor executor;
	
}

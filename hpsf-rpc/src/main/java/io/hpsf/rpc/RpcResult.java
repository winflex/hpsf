/**
 * 
 */
package io.hpsf.rpc;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author winflex
 */
@Data
public class RpcResult implements Serializable {
	
	private static final long serialVersionUID = 5849606699245716833L;

	private Object result;
	
	private Throwable cause;
	
	public boolean isSuccess() {
		return cause == null;
	}
}

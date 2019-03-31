/**
 * 
 */
package lrpc.common;

/**
 *
 * @author winflex
 */
public class RpcResult {
	
	private Object result;
	
	private Throwable cause;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	
	public boolean isSuccess() {
		return cause == null;
	}
}

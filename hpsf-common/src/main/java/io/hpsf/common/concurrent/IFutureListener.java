package io.hpsf.common.concurrent;

/**
 * 
 *
 * @author winflex
 */
public interface IFutureListener<F extends IFuture<?>> {

    void operationCompleted(F future) throws Exception;
}

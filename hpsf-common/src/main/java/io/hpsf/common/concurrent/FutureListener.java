package io.hpsf.common.concurrent;

/**
 * 
 *
 * @author winflex
 */
public interface FutureListener<F extends Future<?>> {

    void operationCompleted(F future) throws Exception;
}

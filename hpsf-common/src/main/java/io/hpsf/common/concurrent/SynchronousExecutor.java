package io.hpsf.common.concurrent;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 当前线程执行
 *
 * @author winflex
 */
public final class SynchronousExecutor implements Executor {

    public static final SynchronousExecutor INSTANCE = new SynchronousExecutor();
    
    private SynchronousExecutor() {
        // use static instance instead
    }
    
    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command, "command").run();
    }
}

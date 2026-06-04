package org.example.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

    private final AtomicInteger counter = new AtomicInteger(1);
    private final String poolName;

    public CustomThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = poolName + "-worker-" + counter.getAndIncrement();

        System.out.println("[ThreadFactory] Creating new thread: " + threadName);

        Thread thread = new Thread(runnable, threadName);

        thread.setUncaughtExceptionHandler((t, e) ->
                System.out.println("[ThreadFactory] Uncaught error in "
                        + t.getName() + ": " + e.getMessage())
        );

        return thread;
    }
}
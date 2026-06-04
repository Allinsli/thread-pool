package org.example.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {

    private final BlockingQueue<Runnable> queue;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final AtomicInteger workerCount;
    private final int corePoolSize;
    private final CustomThreadPool pool;

    public Worker(
            BlockingQueue<Runnable> queue,
            long keepAliveTime,
            TimeUnit timeUnit,
            AtomicInteger workerCount,
            int corePoolSize,
            CustomThreadPool pool
    ) {
        this.queue = queue;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.workerCount = workerCount;
        this.corePoolSize = corePoolSize;
        this.pool = pool;
    }

    @Override
    public void run() {
        String workerName = Thread.currentThread().getName();

        try {
            while (true) {
                if (pool.isShutdown() && queue.isEmpty()) {
                    break;
                }

                Runnable task = queue.poll(keepAliveTime, timeUnit);

                if (task == null) {
                    if (workerCount.get() > corePoolSize) {
                        System.out.println("[Worker] " + workerName + " idle timeout, stopping.");
                        break;
                    }
                    continue;
                }

                if (!pool.isShutdown()) {
                    System.out.println("[Worker] " + workerName + " executes " + task);
                    task.run();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Worker] " + workerName + " interrupted.");
        } finally {
            workerCount.decrementAndGet();
            System.out.println("[Worker] " + workerName + " terminated.");
        }
    }
}
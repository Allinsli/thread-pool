package org.example.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPool implements CustomExecutor {

    private final int corePoolSize;
    private final int maxPoolSize;
    private final int queueSize;
    private final int minSpareThreads;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;

    private final List<BlockingQueue<Runnable>> queues = new ArrayList<>();
    private final List<Thread> workers = new ArrayList<>();

    private final CustomThreadFactory threadFactory;
    private final RejectedTaskHandler rejectedTaskHandler;

    private final AtomicInteger workerCount = new AtomicInteger(0);
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    private volatile boolean shutdown = false;

    public CustomThreadPool(
            int corePoolSize,
            int maxPoolSize,
            long keepAliveTime,
            TimeUnit timeUnit,
            int queueSize,
            int minSpareThreads,
            String poolName,
            RejectedTaskHandler rejectedTaskHandler
    ) {
        if (corePoolSize <= 0) {
            throw new IllegalArgumentException("corePoolSize must be greater than 0");
        }
        if (maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maxPoolSize must be greater than or equal to corePoolSize");
        }
        if (queueSize <= 0) {
            throw new IllegalArgumentException("queueSize must be greater than 0");
        }
        if (minSpareThreads < 0) {
            throw new IllegalArgumentException("minSpareThreads must not be negative");
        }

        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.queueSize = queueSize;
        this.minSpareThreads = minSpareThreads;
        this.threadFactory = new CustomThreadFactory(poolName);
        this.rejectedTaskHandler = rejectedTaskHandler;

        for (int i = 0; i < corePoolSize; i++) {
            addWorker();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException("command must not be null");
        }

        if (shutdown) {
            rejectedTaskHandler.reject(command);
            return;
        }

        ensureSpareThreads();

        BlockingQueue<Runnable> queue = chooseQueue();

        if (queue.offer(command)) {
            int queueId = queues.indexOf(queue);
            System.out.println("[Pool] Task accepted into queue #" + queueId + ": " + command);
            return;
        }

        if (workerCount.get() < maxPoolSize) {
            addWorker();
            BlockingQueue<Runnable> newQueue = queues.get(queues.size() - 1);

            if (newQueue.offer(command)) {
                int queueId = queues.indexOf(newQueue);
                System.out.println("[Pool] Task accepted into new queue #" + queueId + ": " + command);
                return;
            }
        }

        rejectedTaskHandler.reject(command);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        if (callable == null) {
            throw new NullPointerException("callable must not be null");
        }

        FutureTask<T> futureTask = new FutureTask<>(callable);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public void shutdown() {
        shutdown = true;
        System.out.println("[Pool] Shutdown started. Waiting for queued tasks to finish.");
    }

    @Override
    public void shutdownNow() {
        shutdown = true;
        System.out.println("[Pool] ShutdownNow started. Interrupting workers.");

        synchronized (workers) {
            for (Thread worker : workers) {
                worker.interrupt();
            }
        }

        for (BlockingQueue<Runnable> queue : queues) {
            queue.clear();
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    private void ensureSpareThreads() {
        int freeThreads = countFreeThreads();

        if (freeThreads < minSpareThreads && workerCount.get() < maxPoolSize) {
            addWorker();
        }
    }

    private int countFreeThreads() {
        int free = 0;

        for (BlockingQueue<Runnable> queue : queues) {
            if (queue.isEmpty()) {
                free++;
            }
        }

        return free;
    }

    private BlockingQueue<Runnable> chooseQueue() {
        int index = Math.abs(roundRobinIndex.getAndIncrement());

        synchronized (queues) {
            return queues.get(index % queues.size());
        }
    }

    private void addWorker() {
        synchronized (workers) {
            if (workerCount.get() >= maxPoolSize) {
                return;
            }

            BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
            queues.add(queue);

            Worker worker = new Worker(
                    queue,
                    keepAliveTime,
                    timeUnit,
                    workerCount,
                    corePoolSize,
                    this
            );

            Thread thread = threadFactory.newThread(worker);
            workers.add(thread);
            workerCount.incrementAndGet();
            thread.start();
        }
    }
}
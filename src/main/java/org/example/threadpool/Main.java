package org.example.threadpool;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        CustomThreadPool pool = new CustomThreadPool(
                2,                      // corePoolSize
                4,                      // maxPoolSize
                5,                      // keepAliveTime
                TimeUnit.SECONDS,
                5,                      // queueSize
                1,                      // minSpareThreads
                "MyPool",
                new AbortRejectedTaskHandler()
        );

        for (int i = 1; i <= 30; i++) {
            try {
                pool.execute(new NamedTask("Task-" + i, 5000));
            } catch (Exception e) {
                System.out.println("[Main] " + e.getMessage());
            }
        }

        Thread.sleep(15000);

        pool.shutdown();

        System.out.println("[Main] Shutdown requested.");
    }
}
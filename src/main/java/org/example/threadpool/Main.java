package org.example.threadpool;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        CustomThreadPool pool = new CustomThreadPool(
                2,
                4,
                5,
                TimeUnit.SECONDS,
                5,
                1,
                "MyPool",
                new AbortRejectedTaskHandler()
        );

        try {
            Future<String> future = pool.submit(() -> {
                Thread.sleep(1000);
                return "Callable completed";
            });

            System.out.println("[Main] " + future.get());
        } catch (Exception e) {
            System.out.println("[Main] Callable error: " + e.getMessage());
        }

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
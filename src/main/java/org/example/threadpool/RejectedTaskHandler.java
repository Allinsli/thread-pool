package org.example.threadpool;

public interface RejectedTaskHandler {

    void reject(Runnable task);
}
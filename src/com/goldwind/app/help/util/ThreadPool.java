package com.goldwind.app.help.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final ThreadPool manager = new ThreadPool();
    private ExecutorService service;

    private ThreadPool() {
        service = Executors.newFixedThreadPool(3);
    }

    public static ThreadPool getInstance() {
        return manager;
    }

    public void addTask(Runnable runnable) {
        service.execute(runnable);
    }
}

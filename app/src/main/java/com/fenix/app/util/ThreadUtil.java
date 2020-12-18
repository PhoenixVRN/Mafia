package com.fenix.app.util;

import com.crawlink.Promise;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

public final class ThreadUtil {

    public static void Await(Runnable runnable) {
        AtomicReference<Throwable> internalExceptionBox = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                runnable.run();
            } catch (Throwable ex) {
                internalExceptionBox.set(ex);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Throwable internalException = internalExceptionBox.get();
        if (internalException != null)
            throw new RuntimeException(internalException);
    }

    public static Promise Do(Runnable runnable) {
        Promise p = new Promise();
        new Thread(() -> {
            try {
                runnable.run();
            } catch (Throwable ex) {
                p.reject(ex);
            }
            p.resolve(null);
        }).start();
        return p;
    }

    public static Promise Do(Callable  callable) {
        Promise p = new Promise();
        new Thread(() -> {
            try {
                p.resolve(callable.call());
            } catch (Throwable ex) {
                p.reject(ex);
            }
        }).start();
        return p;
    }
}

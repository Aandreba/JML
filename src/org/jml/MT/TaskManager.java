package org.jml.MT;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TaskManager extends ArrayList<Runnable> implements Runnable {
    final public static int THREADS = Runtime.getRuntime().availableProcessors();
    final public static int AV_THREADS = THREADS - 1;

    public TaskManager (Runnable... tasks) {
        super();
        Collections.addAll(this, tasks);
    }

    public void run () {
        AtomicInteger pos = new AtomicInteger(0);
        int size = size();

        // Initialize threads
        Thread[] threads = new Thread[AV_THREADS];
        for (int i=0;i<AV_THREADS;i++) {
            threads[i] = new Thread(() -> {
                int j;
                while ((j = pos.incrementAndGet()) < size) {
                    get(j).run();
                }
            });

            threads[i].start();
        }

        while (true) {
            boolean done = true;
            for (Thread thread: threads) {
                if (thread.isAlive()) {
                    done = false;
                    break;
                }
            }

            if (done) {
                break;
            }
        }
    }
}

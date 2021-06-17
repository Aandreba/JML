package org.jml.Mathx;

import java.util.*;
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
        // Assign tasks to each thread
        int tasksPerThread = size() / AV_THREADS;
        int rest = size() - (tasksPerThread * AV_THREADS);

        Runnable[][] tasks = new Runnable[AV_THREADS][];
        for (int i=0;i<AV_THREADS;i++) {
            int n = i == 0 ? tasksPerThread + rest : tasksPerThread;
            tasks[i] = new Runnable[n];

            int delta = i == 0 ? 0 : rest + tasksPerThread * i;
            for (int j=0;j<n;j++) {
                tasks[i][j] = get(delta + j);
            }
        }

        // Initialize threads
        Thread[] threads = new Thread[AV_THREADS];
        for (int i=0;i<AV_THREADS;i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                Runnable[] tsks = tasks[finalI];
                for (Runnable tsk : tsks) {
                    tsk.run();
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

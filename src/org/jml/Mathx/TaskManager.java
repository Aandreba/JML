package org.jml.Mathx;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TaskManager extends ArrayList<TaskManager.Task> implements Runnable {
    public static class Task {
        final public Runnable run;
        final public int priority;

        public Task (Runnable run, int priority) {
            this.run = run;
            this.priority = priority;
        }

        public Task (Runnable run) {
            this.run = run;
            this.priority = 0;
        }
    }

    final public static int THREADS = Runtime.getRuntime().availableProcessors();
    final public static int AV_THREADS = THREADS - 1;

    public TaskManager (Task... tasks) {
        super();
        Collections.addAll(this, tasks);
    }

    public void run () {
        // Pre-Run Sorting by priority
        sort(Comparator.comparingInt(x -> x.priority));

        // Assign tasks to each thread
        int tasksPerThread = size() / AV_THREADS;
        int rest = size() - (tasksPerThread * AV_THREADS);

        Task[][] tasks = new Task[AV_THREADS][];
        for (int i=0;i<AV_THREADS;i++) {
            int n = i == 0 ? tasksPerThread + rest : tasksPerThread;
            tasks[i] = new Task[n];

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
                Task[] tsks = tasks[finalI];
                for (Task tsk : tsks) {
                    tsk.run.run();
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

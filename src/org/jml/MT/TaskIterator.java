package org.jml.MT;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskIterator implements Runnable {
    public interface Condition {
        boolean apply (int epoch);
    }

    public interface Runner {
        void apply (int epoch);
    }

    final public Runner runner;
    public Condition condition;

    public TaskIterator (Runner runner, Condition cond) {
        super();
        this.runner = runner;
        this.condition = cond;
    }

    public TaskIterator (Runnable run, Condition condition) {
        this(i -> run.run(), condition);
    }

    public void run () {
        AtomicInteger epochs = new AtomicInteger(0);

        // Initialize threads
        Thread[] threads = new Thread[TaskManager.AV_THREADS];
        for (int i=0;i<TaskManager.AV_THREADS;i++) {
            threads[i] = new Thread(() -> {
                int j;
                while (condition.apply(j = epochs.incrementAndGet())) {
                    runner.apply(j);
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

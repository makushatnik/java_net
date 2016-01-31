package nio;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
public class ThreadPool extends Thread {
    private static final int DEFAULT_NUM_WORKERS = 5;
    private LinkedList<Worker> workerPool = new LinkedList<>();
    private LinkedList<Runnable> taskList = new LinkedList<>();
    private boolean stopped = false;

    public ThreadPool() {
        this(DEFAULT_NUM_WORKERS);
    }

    public ThreadPool(int num) {
        for (int i=0; i < num; i++)
            workerPool.add(new Worker("worker" + i, this));
        start();
    }

    public void run() {
        try {
            while (!stopped) {
                if (taskList.isEmpty()) {
                    synchronized (taskList) {
                        taskList.wait();
                    }
                }
                else if (workerPool.isEmpty()) {
                    synchronized (workerPool) {
                        workerPool.wait();
                    }
                }
                getWorker().setTask((Runnable)taskList.removeLast());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTask(Runnable task) {
        taskList.addFirst(task);
        synchronized (taskList) {
            taskList.notify();
        }
    }

    public void putWorker(Worker worker) {
        workerPool.addFirst(worker);
        synchronized (workerPool) {
            workerPool.notify();
        }
    }

    private Worker getWorker() {
        return (Worker)workerPool.removeLast();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stopThreads() {
        stopped = true;
        Iterator it = workerPool.iterator();
        while (it.hasNext()) {
            Worker w = (Worker)it.next();
            synchronized (w) {
                w.notify();
            }
        }
    }

    public void testThreadPool() {
        ThreadPool tp = new ThreadPool();
        for (int i=0; i < 10; i++) {
            tp.addTask(new Runnable() {
                @Override
                public void run() {
                    System.out.println("A");
                }
            });
        }
        tp.stopThreads();
    }
}

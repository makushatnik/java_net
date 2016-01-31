package nio;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Ageev Evgeny on 31.01.2016.
 */
public class Worker extends Thread {
    public static final Logger logger = Logger.getLogger("Worker");
    private String workerId;
    private Runnable task;
    private ThreadPool threadPool;

    static {
        try {
            logger.setUseParentHandlers(false);
            FileHandler ferr = new FileHandler("WorkerErr.log");
            ferr.setFormatter(new SimpleFormatter());
            logger.addHandler(ferr);
        } catch (IOException e) {
            System.out.println("Logger not initialized..");
        }
    }

    public Worker(String id, ThreadPool pool) {
        workerId = id;
        threadPool = pool;
        start();
    }

    public void setTask(Runnable t) {
        task = t;
        synchronized (this) {
            notify();
        }
    }

    public void run() {
        try {
            while (!threadPool.isStopped()) {
                synchronized (this) {
                    if (task != null) {
                        try {
                            task.run();
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Exception in source Runnable task", e);
                        }
                        threadPool.putWorker(this);
                    }
                    wait();
                }
            }
            System.out.println(this + " Stopped");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String toString() {
        return "Worker : " + workerId;
    }
}

package main;

import java.util.Observable;

/**
 * Created by Ageev Evgeny on 27.03.2016.
 */
public class Download extends Observable implements Runnable {

    private static final int MAX_BUFFER_SIZE = 1024;

    public static final String STATUSES[] = {
            "Not started",
            "Downloading",
            "Paused",
            "Complete",
            "Cancelled",
            "Error"
    };

    public static final int NOT_STARTED = 0;
    public static final int DOWNLOADING = 1;
    public static final int PAUSED = 2;
    public static final int COMPLETE = 3;
    public static final int CANCELLED = 4;
    public static final int ERROR = 5;

    private String fname;
    private long size;
    private int downloaded;
    private int status;

    public Download(String fname) {
        this.fname = fname;
        size = -1;
        downloaded = 0;
        status = NOT_STARTED;
    }

    public String getFname() {
        return fname;
    }

    public long getSize() {
        return size;
    }

    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getStatus() {
        return status;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDownloaded(int dl) {
        this.downloaded = dl;
    }

    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        //download();
    }

    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

    private void error() {
        status = ERROR;
        stateChanged();
    }

    /*private String getFileName() {
        return fname.substring(fname.lastIndexOf('/') + 1);
    }*/

    public void run() {

    }

    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}
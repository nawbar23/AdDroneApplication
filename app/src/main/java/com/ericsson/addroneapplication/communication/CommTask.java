package com.ericsson.addroneapplication.communication;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nbar on 2016-08-30.
 * Abstract class for all synchronous communication tasks.
 * Controlled by start and stop methods, dynamically responds for frequency change.
 */
public abstract class CommTask {
    private static final String DEBUG_TAG = "AdDrone:" + CommTask.class.getSimpleName();

    private CommHandler commHandler;

    private Timer timer;

    // frequency of task [Hz]
    private double frequency;

    private boolean isRunning;

    protected CommTask(CommHandler commHandler, double frequency) {
        this.commHandler = commHandler;
        this.frequency = frequency;
        this.isRunning = false;
    }

    public void start() {
        start(frequency);
    }

    private void start(double freq) {
        timer = new Timer(getTaskName() + "_timer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task();
            }
        };
        long period = (long)((1.0 / freq) * 1000);
        long delay = period > 1000 ? period : 1000;
        //Log.e(DEBUG_TAG, "Starting " + getTaskName() + " task with freq: " + String.valueOf(freq) + " Hz, and delay: " + String.valueOf(delay) + " ms");
        timer.scheduleAtFixedRate(timerTask, delay, period);
        isRunning = true;
    }

    public void stop() {
        timer.cancel();
        isRunning = false;
    }

    public void restart() {
        restart(frequency);
    }

    private void restart(double freq) {
        stop();
        start(freq);
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        stop();
        start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected abstract String getTaskName();

    protected abstract void task();
}

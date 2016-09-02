package com.ericsson.addroneapplication.comunication;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nbar on 2016-08-30.
 * Abstract class for all synchronous communication tasks.
 * Controlled by start and stop methods, dynamically responds for frequency change.
 */

public abstract class CommunicationTask {
    private static final String DEBUG_TAG = "AdDrone:" + CommunicationTask.class.getSimpleName();

    protected CommunicationHandler communicationHandler;
    protected TcpSocket tcpSocket;

    private Timer timer;

    // frequency of task [Hz]
    protected double frequency;

    private boolean isRunning;

    CommunicationTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double frequency) {
        this.communicationHandler = communicationHandler;
        this.tcpSocket = tcpSocket;
        this.frequency = frequency;
        this.isRunning = false;
    }

    public void start() {
        start(frequency);
    }

    protected void start(double freq) {
        this.timer = new Timer(getTaskName() + "_timer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task();
            }
        };
        long period = (long)((1.0 / freq) * 1000);
        long delay = period > 1000 ? period : 1000;
        Log.e(DEBUG_TAG, "Starting " + getTaskName() + " task with freq: " + String.valueOf(freq) + " Hz, and delay: " + String.valueOf(delay) + " ms");
        this.timer.scheduleAtFixedRate(timerTask, delay, period);
        this.isRunning = true;
    }

    public void stop() {
        this.timer.cancel();
        this.isRunning = false;
    }

    public void restart() {
        restart(frequency);
    }

    protected void restart(double freq) {
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

    abstract String getTaskName();

    abstract void task();
}

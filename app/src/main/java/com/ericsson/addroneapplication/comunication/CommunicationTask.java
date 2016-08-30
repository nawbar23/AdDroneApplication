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

    CommunicationTask(CommunicationHandler communicationHandler, TcpSocket tcpSocket, double frequency) {
        this.communicationHandler = communicationHandler;
        this.tcpSocket = tcpSocket;
        this.frequency = frequency;
    }

    public void start() {
        start(frequency);
    }

    protected void start(double freq) {
        Log.e(DEBUG_TAG, "Starting " + getTaskName() + " task with freq: " + String.valueOf(freq) + " Hz");
        this.timer = new Timer(getTaskName() + "_timer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task();
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 1000, (long)((1.0 / freq) * 1000));
    }

    public void stop() {
        this.timer.cancel();
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

    abstract String getTaskName();

    abstract void task();
}

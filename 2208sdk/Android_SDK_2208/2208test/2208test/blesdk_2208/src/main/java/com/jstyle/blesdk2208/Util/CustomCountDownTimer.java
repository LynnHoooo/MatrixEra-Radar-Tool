package com.jstyle.blesdk2208.Util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 计时器
 */
public class CustomCountDownTimer {
    public static final String TAG = "CustomCountDownTimer";

    //thread on which the callbacks will be called
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    //listener interface which is to be implemented by the users of the count down timer
    public interface TimerTickListener {
        /**
         * Callback on each tick
         *
         * @param millisLeft time left in millisec for the timer to shutdown
         */
        void onTick(long millisLeft);

        /**
         * Callback to be invokded when timer's time finishes
         */
        void onFinish();

        /**
         * Callback to be invokded when timer is canceled
         */
        void onCancel();
    }

    /**
     * Inner class which delegates the events to callbacks provided in the TimerTickListener
     */
    private class TimerRunnable implements Runnable {
        public void run() {
            if (isCancelled) {
                scheduler.shutdown();
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tickListener.onCancel();
                    }
                });
            } else if (isPaused) {
            } else {
                stopTimeInFuture = stopTimeInFuture - countdownInterval;
                mainThreadHandler.post(new Runnable() {
                    final long millisLeft = stopTimeInFuture;

                    @Override
                    public void run() {
                        if (millisLeft <= 0) {
                            tickListener.onFinish();
                            scheduler.shutdown();
                        } else {
                            tickListener.onTick(millisLeft);
                        }
                    }
                });
            }
        }
    }

    //Millis since epoch when alarm should stop.
    private long millisInFuture;

    //The interval in millis that the user receives callbacks
    private final long countdownInterval;

    //the time at which timer is to stop
    private long stopTimeInFuture;

    //boolean representing if the timer was cancelled
    public boolean isCancelled = false;

    //boolean representing if the timer was paused
    private boolean isPaused = false;

    //listener which listens to the timer events
    private final TimerTickListener tickListener;

    //scheduler which provides the thread to create timer
    private final ScheduledExecutorService scheduler;

    /**
     * Constructor
     *
     * @param millisInFuture    time in millisec for which timer is to run
     * @param countDownInterval interval frequency in millisec at which the callback will be invoked
     * @param tickListener      implementation of TimerTickListener which provides callbacks code
     */
    public CustomCountDownTimer(long millisInFuture, long countDownInterval,
                                TimerTickListener tickListener) {
        this.millisInFuture = millisInFuture;
        stopTimeInFuture = millisInFuture;
        countdownInterval = countDownInterval;
        this.tickListener = tickListener;
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Start the countdown.
     */
    public synchronized void start() {
        isCancelled = false;
        isPaused = false;
        scheduler.scheduleWithFixedDelay(new TimerRunnable(), countdownInterval, countdownInterval,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Cancels the countdown timer
     */
    public synchronized final void cancel() {
        isCancelled = true;
    }

    public synchronized final void pause() {
        isPaused = true;
    }

    public synchronized final void resume() {
        isPaused = false;
    }

    public void extendTime(long delta) {
        stopTimeInFuture = stopTimeInFuture + delta;
        millisInFuture = millisInFuture + delta;
    }
}
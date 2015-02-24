/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * Simple Looping Service. Useful for games, Simulations, or other items that require a running "Loop".
 *
 * @author Jason Pollastrini aka jdub1581
 */
public abstract class AbstractLoopService extends ScheduledService<Void>{
    private final long ONE_NANO = 1_000_000_000L;
    private final double ONE_NANO_INV = 1f / 1_000_000_000L;

    private long startTime, previousTime;
    private double frameRate, deltaTime;
        
    private final LoopThreadFactory tf = new LoopThreadFactory();    
    private final ExecutorService cachedExecutor = Executors.newCachedThreadPool(tf);

    
    protected AbstractLoopService() {
        this.setPeriod(Duration.millis(16.667)); // eqiv to 60 fps
        this.setExecutor(cachedExecutor);
    }


    protected final double getTimeElapsed() {
        return getCurrentTime() * ONE_NANO_INV;
    }

    protected final long getCurrentTime() {
        return System.nanoTime() - startTime;
    }

    protected final double getFrameRate() {
        return frameRate;
    }

    protected final double getDeltaTime() {
        return deltaTime;
    }

    private void updateTimer() {
        deltaTime = (getCurrentTime() - previousTime) * (1.0f / ONE_NANO);
        frameRate = 1.0f / deltaTime;
        previousTime = getCurrentTime();

    }

    @Override
    public void start() {
        super.start();
        if (startTime <= 0) {
            startTime = System.nanoTime();
        }
    }

    @Override
    public void reset() {
        super.reset();
        startTime = System.nanoTime();
        previousTime = getCurrentTime();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateTimer();
                // perform needed background tasks here ..
                runInBackground();
                
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        // Setup to handle Actions for UI here     
        runOnFXThread();
    }

    @Override
    protected void failed() {
        getException().printStackTrace(System.err);

    }

    @Override
    public String toString() {
        return "ElapsedTime: " + getCurrentTime() + "\nTime in seconds: " + getTimeElapsed()
                + "\nFrame Rate: " + getFrameRate()
                + "\nDeltaTime: " + getDeltaTime();
    }
    
    /*==========================================================================
     *      Methods for access
     */
    
    protected abstract void runOnFXThread();
    protected abstract void runInBackground();

    /*==========================================================================
    
     */
    private final class LoopThreadFactory implements ThreadFactory {

        public LoopThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "NanoTimerThread");
            t.setPriority(Thread.NORM_PRIORITY + 1);
            t.setDaemon(true);
            return t;
        }

    }
    
}

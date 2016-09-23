/**
 * AbstractLoopService.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

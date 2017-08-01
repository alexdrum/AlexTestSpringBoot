package com.alextest.crawler.thread;

import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;

public abstract class SuperTask implements Runnable {

    private StopWatch stopWatch;
    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    protected void workStart() {
        stopWatch.start();
    }

    protected void workDone() {
        this.countDownLatch.countDown();
        stopWatch.stop();
    }

    @Override
    public abstract void run();
}

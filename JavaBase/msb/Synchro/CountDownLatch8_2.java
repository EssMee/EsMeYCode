package com.algorithm.zy.msb.gaobingfa;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchTest {
    static CountDownLatch c = new CountDownLatch(2);
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("解析第一个sheet。。。");
                c.countDown();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("解析第二个sheet。。。");
                c.countDown();
            }
        }).start();
        c.await();
        System.out.println("解析完成。。");
    }
}

/*
class Worker implements Runnable {
    private final CountDownLatch startSignal;
    private final CountDownLatch endSignal;
    public Worker(CountDownLatch startSignal, CountDownLatch endSignal) {
        this.startSignal = startSignal;
        this.endSignal = endSignal;
    }

    @Override
    public void run() {
        try {
            startSignal.await();
            System.out.println("startSignal await at " + new Timestamp(System.currentTimeMillis()));
            endSignal.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}*/

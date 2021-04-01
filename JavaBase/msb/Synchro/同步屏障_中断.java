package com.algorithm.zy.msb.gaobingfa;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest3 {
    private static final CyclicBarrier c = new CyclicBarrier(3);

    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();
                    System.out.println("子线程进入屏障");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("子线程出屏障");
            }
        },"子线程");
        t.start();
        t.interrupt();
        try {
            c.await();
            System.out.println("主线程进入屏障");
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("阻塞的线程是否被中断？ "+ c.isBroken() + "，线程名：" + Thread.currentThread().getName());
        }
    }
}

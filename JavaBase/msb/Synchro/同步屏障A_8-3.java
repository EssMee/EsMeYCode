package com.algorithm.zy.msb.gaobingfa;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CycliBarrierTest {
    /*表示一共有两个线程会到达屏障*/
    static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();
                    System.out.println("子线程到屏障了");
                } catch (InterruptedException e) {

                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("子线程出屏障了");
            }
        }).start();

        try {
            c.await();
            System.out.println("主线程到屏障了");
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程出屏障了");
    }
}

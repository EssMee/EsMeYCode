package com.algorithm.zy.msb.gaobingfa;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest2 {
    static CyclicBarrier c = new CyclicBarrier(2, new A());

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();
                    System.out.println("子线程到屏障了");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("子线程出屏障了");
            }
        }).start();

        try {
            c.await();
            System.out.println("主线程到屏障了");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("主线程出屏障了");
    }
    private static class A implements Runnable {
        @Override
        public void run() {
            System.out.println("A线程应该先打印出来");
        }
    }
}

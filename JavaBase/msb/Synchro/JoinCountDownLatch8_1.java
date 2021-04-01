package com.algorithm.zy.msb.gaobingfa;

public class JoinCountDownLatchTest {
    /*使用join完成类似于countdownlatch的操作*/
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("解析第一个sheet。。。。");
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("解析第二个sheet。。。。");
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("解析完成");
    }
}

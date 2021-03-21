package com.algorithm.zy.msb;
/*synchronized作用于一个类T时，是给这个类T加锁，作用的是这个类T的所有对象*/
public class Demo06 implements Runnable {
    private static int count;
    public Demo06() {count = 0;}
    public static void method() {
        synchronized (Demo06.class) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + ":" + (count ++));
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        method();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            Demo06 obj = new Demo06();
            Thread t = new Thread(obj, "Thread" + i);
            t.start();
        }
    }
}

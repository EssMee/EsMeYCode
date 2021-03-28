package com.algorithm.zy.msb.gaobingfa;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/*创建了两个线程WaitThread和NotifyThread，前者检查
flag是否为false，如果符合要求，则进行后续操作，否则
在lock上等待；后者在睡眠了一段时间之后，对lock进行通知。*/
public class WaitNotify4_11 {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(new Wait(), "waitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(), "notifyThread");
        notifyThread.start();

    }

    static class Wait implements Runnable {

        @Override
        public void run() {
            /*加锁，拥有lock的monitor*/
            synchronized (lock) {
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true, continue wait @ " +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /*条件满足时，完成工作*/
                System.out.println(Thread.currentThread() + " flag is false, running @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable {

        @Override
        public void run() {
            /*加锁，拥有lock的monitor*/
            synchronized (lock) {
                try {
                /*获取lock的锁，然后进行通知，通知时不会释放lock的锁，
                直到当前线程释放了lock后，WaitThread才能从wait方法中返回。*/
                    System.out.println(Thread.currentThread() + " hold the lock. notify @ " +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    /*【注意】notify调用之后，等待在对象上的线程并不会立刻从wait返回，而是需要调用
                    notify的线程释放锁之后，等待线程才有机会从wait返回*/
                    lock.notifyAll();
                    flag = false;
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            synchronized (lock) {
                try {
                    System.out.println(Thread.currentThread() + " hold the lock again. sleep @ " +
                            new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.algorithm.zy.msb;
/*Demo05：修饰静态方法
起作用的范围是整个静态方法，作用的对象是这个类的所有对象；
我们知道静态方法是属于类的而不是对象的。同样，synchronized修饰的静态方法锁定的是这个类的所有对象。*/
public class Demo05 extends Thread{
    private static int count;
    public Demo05() {
        this.count = 0;
    }
    public synchronized static void method() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println(Thread.currentThread().getName() + ":" + (count ++));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void run() {
        method();
    }

    public static void main(String[] args) {
        Demo05 demo051 = new Demo05();
        Demo05 demo052 = new Demo05();
        Thread t1 = new Thread(demo051, "SynThread1");
        Thread t2 = new Thread(demo052, "SynThread2");
        t1.start();
        t2.start();
    }
}

package com.algorithm.zy.msb;

import java.util.concurrent.TimeUnit;

/*懒汉模式： 有人调用的时候才会new一个INSTANCE出来
* 线程不安全，肯定不能保证只new同一个对象。
* 可以打印一下每个对象的hashCode。
* 缺点是synchronized锁住了整个getInstance方法，如果需要在里面有业务代码时，业务代码也会被锁住。
* */
public class SingleTonMode2 {
    private static SingleTonMode2 INSTANCE;

    private SingleTonMode2() {
    }

    /*如果这个方法加了synchronized，那100次new出来的对象肯定是同一个。*/
    public synchronized static SingleTonMode2 getInstance() {
        if (INSTANCE == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            INSTANCE = new SingleTonMode2();
        }
        return INSTANCE;
    }

    public void m() {
        System.out.println("mmmmmmmmmmmmmm");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                System.out.println(SingleTonMode2.getInstance().hashCode());
            }).start();
        }
    }
}


package com.algorithm.zy.msb;

/*  Double Check Lock 单例模式
    与SingleTonMode2相比，现在是对类的修饰：作用的是静态方法所在类的所有对象（或者说对象引用）。
    而没有锁住genInstance方法。    
* */
public class SingleTonDoubleCheckLockMode {
    private static  volatile SingleTonDoubleCheckLockMode INSTANCE;
    private SingleTonDoubleCheckLockMode(){}
    public static SingleTonDoubleCheckLockMode getInstance() {
        if (INSTANCE == null) {
            synchronized (SingleTonDoubleCheckLockMode.class) {
                if (INSTANCE == null) {
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    INSTANCE = new SingleTonDoubleCheckLockMode();
                }
            }
        }
        return INSTANCE;
    }

    public void m() {
        System.out.println("mmmmmmmmmmmm");
    }

    public static void main(String[] args) { ;
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                SingleTonDoubleCheckLockMode t = SingleTonDoubleCheckLockMode.getInstance();
                System.out.println(t.hashCode());
            }).start();
        }
    }
}


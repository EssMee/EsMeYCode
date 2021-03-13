package com.algorithm.zy.msb;
/*
饿汉单例模式：不管其他人有没有调用getInstance，在类初始化的时候就会先new出一个INSTANCE*/
public class SingleTonMode1 {
    private static final SingleTonMode1 INSTANCE = new SingleTonMode1();
    private SingleTonMode1(){}

    public static SingleTonMode1 getInstance() {
        return INSTANCE;
    }
    public void m() {
        System.out.println("mmmmmmmmmmmmm");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                SingleTonMode1 t = SingleTonMode1.getInstance();
                System.out.println(t.hashCode());
            }).start();
        }
    }
}

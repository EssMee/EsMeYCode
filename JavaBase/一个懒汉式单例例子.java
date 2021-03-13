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
        SingleTonMode1 sm1 = SingleTonMode1.getInstance();
        SingleTonMode1 sm2 = SingleTonMode1.getInstance();
        System.out.println(sm1.equals(sm2));
    }
}

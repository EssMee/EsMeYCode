package com.flink.zy;

public class test {
    public static void main(String[] args) {
        MySource.FileRead fr = new MySource.FileRead("D:\\easonFlinkTest\\src\\main\\resources\\session.txt");
        System.out.println(fr.readFile());
    }
}

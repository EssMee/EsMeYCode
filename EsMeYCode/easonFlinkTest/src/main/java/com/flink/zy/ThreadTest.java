package com.flink.zy;

public class ThreadTest implements Runnable {
    public static void main(String[] args) {
        ThreadTest t = new ThreadTest();
        t.run();
    }

    @Override
    public void run() {
        try {
            while (true) {
                MySource.FileRead fr = new MySource.FileRead("D:\\EsMeYCode\\easonFlinkTest\\src\\main\\resources\\session.txt");
                System.out.println(fr.readFile());
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

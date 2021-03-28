package msb.Synchro;

/*
demo01
一个线程访问同一个对象的synchronized(this)同步代码块时，其他试图访问该对象的线程
将被阻塞。
*/
public class demo01 extends Thread{
    private static int count;
    private static String name;
    public demo01(){
        this.count = 0;
    }
    public void run() {
        synchronized (this) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + ":" + (count++));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public int getCount() {
        return count;
    }

    public static void main(String[] args) {
 /*       System.out.println("只有一把锁的情况");
        demo01 demo= new demo01();
        Thread thread1 = new Thread(demo, "SyncThread1");
        Thread thread2 = new Thread(demo, "SyncThread2");
        thread1.start();
        thread2.start();*/
        System.out.println("--------------分割线--------------");
        System.out.println("有两把锁的情况");
        demo01 d1 = new demo01();
        demo01 d2 = new demo01();
/*        d1.start();
        d2.start();*/
        Thread t1 = new Thread(d1, "SyncThread1");
        Thread t2 = new Thread(d2, "SyncThread2");
        t1.start();
        t2.start();
    }
}

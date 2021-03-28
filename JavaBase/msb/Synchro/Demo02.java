package msb.Synchro;

/*demo02
多个线程访问synchronized和非synchronized代码块。
代码中countAdd是一个synchronized的，printCount是非synchronized的。
从结果中可以看出一个线程访问一个对象的synchronized代码块时，别的线程可以访问该对象的
非synchronized代码块而不受阻塞。
*/
public class Demo02 implements Runnable {
    private int count;
    public Demo02() {
        this.count = 0;
    }
    public void countAdd() {
        synchronized (this) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + ":" + (count ++));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*非synchronized代码块，因为未对count做读写操作，所以可以不需要同步*/
    public void printCount() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println(Thread.currentThread().getName() + " count: " + count);
                Thread.sleep(100);
            } catch (InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
    public void run() {
        String threadName = Thread.currentThread().getName();
        if (threadName.equals("A")) {
            countAdd();
        } else if (threadName.equals("B")) {
            printCount();
        }
    }

    public static void main(String[] args) {
        Demo02 demo02 = new Demo02();
        Thread thread1 = new Thread(demo02, "A");
        Thread thread2 = new Thread(demo02, "B");

        thread1.start();
        thread2.start();
    }


}

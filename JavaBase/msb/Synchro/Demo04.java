package com.algorithm.zy.msb;

/*Demo04:修饰一个方法
synchronized修饰方法与修饰代码块类似，只是作用范围不一样：
修饰方法是整个函数，修饰代码块只是括起来的部分。

另外，虽然可以使用synchronized定义方法，但是它不属于方法的一部分，因此synchronized关键字不能被继承。
如果在父类中的某个方法使用了synchronized，而在子类中重写了该方法，在子类中的这个方法默认是不同步的。
而必须显式地在子类地这个方法中加上synchronized关键字才可以。
当然，还可以在子类中调用父类中地相应方法，这样虽然子类的方法不是同步的，单子类调用了父类的同步方法，
因此子类的方法也就相当于同步了。*/
public class Demo04 extends Thread {
    private static int count;
    public Demo04(){this.count = 0;}

    @Override
    public synchronized void run() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println(Thread.currentThread().getName() + ":" + (count ++));
                Thread.sleep(100);
            } catch(InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Demo04 demo04 = new Demo04();
        demo04.start();
    }
}

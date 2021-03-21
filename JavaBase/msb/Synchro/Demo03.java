package com.algorithm.zy.msb;

/*
Demo03
指定给某个对象加锁
在AccountOperator类中的run方法里，我们用synchronized给account对象加锁；
这时，当一个线程访问account对象时，其他试图访问account对象的线程将会被阻塞；
知道该线程访问了account对象结束，也就是说谁拿到那个锁谁就可以运行它所控制的那段代码。
下面例子中的五个线程，最终控制的account的run方法是加了锁的，因此五个线程的存钱取钱和查余额的操作，
实际上每一次都只有一个线程能工作，其他的都被阻塞掉了。
*/
public class Demo03 {
    class Account {
        private String name;
        private float amount;

        Account(String name, float amount) {
            this.name = name;
            this.amount = amount;
        }

        /*存钱*/
        public void deposit(float amt) {
            amount += amt;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void withDraw(float value) {
            amount -= value;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*获取余额*/
        public float getBalance() {
            return amount;
        }
    }

    /*账户操作类*/
    class AccountOperator implements Runnable {
        private Account account;
        private byte[] bytes = new byte[0];

        AccountOperator(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            /*锁住的是整个Account对象*/
            synchronized (account) {
                account.deposit(500);
                System.out.println(Thread.currentThread().getName() + ":" + account.amount);
                account.withDraw(500);
                System.out.println(Thread.currentThread().getName() + ":" + account.amount);
                System.out.println(Thread.currentThread().getName() + ":" + account.getBalance());
            }
        }
    }

    public void test() {
        Account account = new Account("zhangyi", 1000.0f);
        AccountOperator accountOperator = new AccountOperator(account);
        final int num = 5;
        Thread[] threads = new Thread[num];
        for (int i = 0; i < num; i++) {
            threads[i] = new Thread(accountOperator, "Thread" + i);
            threads[i].start();
        }
    }

    public static void main(String[] args) {
        Demo03 demo03 = new Demo03();
        demo03.test();
    }

}

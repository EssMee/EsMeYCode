package com.algorithm.zy.msb.gaobingfa;

/*他的定义：
线程池内部维护了若干个线程，没有任务的时候，这些线程都处于等待的状态。
如果有新任务，就分配一个空闲线程执行。
如果所有线程都处于忙碌状态，新任务要么放入队列等待，要么增加一个新线程处理。*/

/*Java标准库提供了ExecutorService接口表示线程池，提供了几个常用的实现类:
        (1)FixedThreadPool：线程数固定的线程池；
        (2)CachedThreadPool：线程数根据任务动态调整的线程池；
        (3)SingleThreadExecutor：仅单线程执行的线程池。*/

import java.util.concurrent.*;

/*创建这些线程的方法都被封装到Executors类中。*/
public class LXFThreadPool1 {
    public static void main(String[] args) {
        /* （1）
        创建一个只有四个线程的线程池，那么头四个task会一起执行一起结束，后两个
        等结束之后才能执行
        */
        ExecutorService es = Executors.newFixedThreadPool(4);
        /*（2）
        如果是动态调整的，全部六个task可以一起执行和结束
        */
//        ExecutorService es = Executors.newCachedThreadPool();

        /*（3）
        还有一种任务需要定期反复执行，例如每秒刷新证券价格。这种任务本身固定，
        需要反复执行，可以使用ScheduledThreadPool。
        * */
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);
        /*提交一次性任务，在指定延迟后执行【一次】*/
        ses.schedule(new Task("one time"), 1, TimeUnit.SECONDS);
        /*任务2s后开始执行，每3s执行
        * 这里的任务是以固定间隔触发，无论任务执行了多长时间，就算3s
        * 之内没有执行完，时间到了也要继续执行下一个。
        * */
        ses.scheduleAtFixedRate(new Task("fix-rate"), 2, 3, TimeUnit.SECONDS);
        /*任务2s后开始执行，以3s为间隔执行。
        * 这里的任务是上一次任务执行完毕后，等待固定的时间间隔，
        * 再执行下一个任务。
        * */
        ses.scheduleWithFixedDelay(new Task("fix-delay"), 2,3,TimeUnit.SECONDS);
        for (int i = 0; i < 6; i++) {
            es.submit(new Task("" + i));
        }
        es.shutdown();
    }

    private static class Task implements Runnable {
        private final String name;
        public Task(String s) {
            this.name = s;
        }

        @Override
        public void run() {
            System.out.println("start task: " + name);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("end task: " + name);
        }
    }
}

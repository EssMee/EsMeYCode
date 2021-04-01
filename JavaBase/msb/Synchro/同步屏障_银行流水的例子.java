package com.algorithm.zy.msb.gaobingfa;

import java.util.Map;
import java.util.concurrent.*;

public class BankWaterService implements Runnable {
    /*创建4个屏障，处理完之后执行当前类的run方法*/
    private final CyclicBarrier c = new CyclicBarrier(4, this);
    /*假设只有4个sheet，所以只启动4个线程*/
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    /*保存每个线程计算出的银流结果*/
    private final ConcurrentHashMap<String,Integer> sheetBankWaterCount = new ConcurrentHashMap<>();

    private void count() {
        for (int i = 0; i < 4; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    /*计算当前sheet的银流数据*/
                    sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
                    System.out.println(Thread.currentThread().getName() + "正在计算sheet的数据");
                    /*计算完成，插入一个屏障*/
                    try {
                        c.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName() + "结束计算");
                }
            });
        }
        executor.shutdown();
    }

    @Override
    public void run() {
        int result = 0;
        /*汇总每个sheet的数据*/
        for (Map.Entry<String,Integer> entry : sheetBankWaterCount.entrySet()) {
            result += entry.getValue();
        }
        sheetBankWaterCount.put("total", result);
        System.out.println("结果：" + result );
    }

    public static void main(String[] args) {
        BankWaterService bankWaterService = new BankWaterService();

        bankWaterService.count();

    }
}

package com.algorithm.zy.Sort;

public class MaxHeap<Item extends Comparable>  {
    private int count;
    private Item[] data;
    private int capacity;
    /*初始化一个可以容纳capacity个元素的最大堆*/
    public MaxHeap(int capacity) {
        this.capacity = capacity;
    }

    public int size() {return this.count;}
    public boolean isEmpty() {return this.count == 0;}
    /*向最大堆的末尾插入一个元素*/
    public void insert(Item item) {
      assert (count+1 <= capacity);
        data[count+1] = item;
      this.count ++;
      shiftUp(count);
    }

    /*把index的元素移到合适的位置以继续满足最大堆的定义*/
    private void shiftUp(int index) {
        while (index > 1 & data[index].compareTo(data[index/2]) > 0) {
            swap(data, index, index/2);
            index = index /2 ;
        }
    }

    private void swap(Item[] arr, int i, int j) {
        Item t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static void main(String[] args) {
        MaxHeap<Integer> maxHeap = new MaxHeap<>(100);
        int N = 50;
        int M = 100;
        for (int i = 0; i < N; i++) {
            maxHeap.insert((int) Math.random() * M);
        }
        System.out.println(maxHeap.size());
    }
}

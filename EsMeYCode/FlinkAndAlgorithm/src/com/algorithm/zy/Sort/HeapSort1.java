package com.algorithm.zy.Sort;
import com.algorithm.zy.Sort.MaxHeap;
public class HeapSort1 {
/*    通过把一个数组构造成最大堆，然后从大到小调用extractMax，然后倒置，
    达到从小到大排序的目的。*/
    private HeapSort1(){}
    public static void sort(int[] arr) {
        int n = arr.length;
        MaxHeap<Integer> maxHeap = new MaxHeap<>(n);
        for (int i = 0; i < n; i++) {
            maxHeap.insert(arr[i]);
        }
        for (int i = n-1; i >= 0 ; i--) {
            arr[i] = maxHeap.extractMax();
        }
    }
}

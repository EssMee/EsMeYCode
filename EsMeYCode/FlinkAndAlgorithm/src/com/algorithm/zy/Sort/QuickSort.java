package com.algorithm.zy.Sort;

public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {6,5,4,3,2,1};
        new QuickSort().sort(arr);
        for (int i : arr) {
            System.out.print(i + ",");
        }
    }

    // choose a pivot, and divide the arr into < arr[pivot] and > arr[pivot]. called arr-left and arr-right.
    // then use the same method to arr-left and arr-right.
    // the condition which the method ends's arr.length <= 1.
    // 对外提供接口，对arr进行排序，里层调用对arr的0到n-1进行排序。
    public  void sort(int[] arr) {
        int n = arr.length;
        sort(arr, 0, n - 1);
    }

    // 重载，对arr的[l,r]范围内的元素进行排序
    private  void sort(int[] arr, int l, int r) {
        if (l >= r) return;
        // _partition方法返回[l,r]的pivot的索引
        //int p = _partitionVersionI(arr, l, r);
        int p = __partitionII(arr, l, r);
        sort(arr, l, p - 1);
        sort(arr, p + 1, r);
    }

/*
    ----基本快排---
    对arr[l...r]进行partition操作，
    返回p，使得arr[l...p-1] < arr[p], arr[p+1...r] > arr[p]
*/
    private  int _partitionVersionI(int[] arr, int l, int r) {
/*        优化的方法，用一个随机的索引作为标定点，与最左侧的元素交换，避免完全有序的情况下，
        每一次分割的时候有一侧子树为空的情况。*/
        swap( arr, l , (int)(Math.random()*(r-l+1))+l );
        int v = arr[l];
        /*每一次操作，使得arr[l+1...j]<v, arr[j+1,i-1]>v*/
        int j = l;
        for (int i = l+1; i <=r ; i++) {
            if (arr[i] < v) {
                j ++;
                swap(arr, i, j);
            }
        }
        swap(arr,l,j);
        return j;
    }

    /*
    --------双路快排------------
    维持数组分成arr[l+1...i-1]<v, arr[j+1...r]>v。
    j的位置是最后一个小于v的值，与一开始的标定点的值交换位置，返回j为最新的标定点。
     */

    private int __partitionII(int[] arr, int l ,int r) {
        int i = l +1;
        int j = r;
        swap(arr, l, (int)(Math.random()*(r-l+1) + l));
        int v = arr[l];
        while (true) {
            while (i <= r && arr[i] < v) {
                /*是从左往右遍历，边界是r的位置*/
                i ++;
            }
            /*j是从右往左遍历，边界是l+1的位置，l的位置一开始被占了。*/
            while (j >= l + 1 && arr[j] > v) {
                j --;
            }
            if (i > j) {
                break;
            }
            /* 到了某一个地方，i和j对应的值分别是大于v和小于v的。
            * 把i和j对应的值换到该放的位置。
            * */
            swap(arr, i, j);
            i ++;
            j --;
        }
        /*这一次循环结束后，把l索引的值和j索引的值交换位置，保持数组三部分的性质。*/
        swap(arr,l,j);
        return j;
    }

    private  void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}

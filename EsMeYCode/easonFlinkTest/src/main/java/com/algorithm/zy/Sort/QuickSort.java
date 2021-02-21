package com.algorithm.zy.Sort;

/*重要*/
public class QuickSort {
    public static void main(String[] args) {
        int[] arr = {1,6,2,3,8,5,7,4};
        sort(arr);
        for (int i : arr) {
            System.out.print(i + ",");
        }
    }

    public static void sort(int[] arr) {
        int n = arr.length;
        sort(arr, 0, n - 1);
    }

    public static void sort(int[] arr, int l, int r) {
        if (l >= r) return;
        int p = __partition(arr, l, r);
        sort(arr, l, p);
        sort(arr, p + 1, r);
    }

    /*在[l,r]之中找到一个p，使得[l,p-1]索引的值都小于p索引对应的值，[p+1,r]索引的值都大于p索引对应的值*/
    private static int __partition(int[] arr, int l, int r) {
        swap(arr, l, (int) Math.random() * (r - l + 1) + l);
        int target = arr[l];
        int j = l;
        for (int i = l + 1; i <= r; i++) {
            if (arr[i] < target) {
                j++;
                swap(arr, i, j);
            }
            swap(arr, l, j);
        }
        return j;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}

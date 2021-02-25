package com.algorithm.zy.Sort;

public class QuickSort {
    public static void main(String[] args) {
        int[] test = {5, 4, 3, 2, 1};
        new MergeSort().sort(test);
        for (int i : test) {
            System.out.print(i + " ");
        }
    }
    public void sort(int[] arr) {
        int n = arr.length;
        sort(arr, 0,n-1);
    }

    private void sort(int[] arr, int l, int r) {
        if (l >=r ) return;
        int p = __partition(arr,l,r);
        sort(arr, l, p-1);
        sort(arr, p+1,r);
    }

    /*找到一个索引j,使得[l+1,j] < v,[j+1,i-1] > v*/
    private int __partition(int[] arr, int l, int r) {
        int v = arr[l];
        int j = l;
        for (int i = j+1; i <=r ; i++) {
            if (arr[i] > v) {

            }
            if (arr[i] < v) {
                swap(arr, j, i);
                j ++;
            }
        }
        swap(arr,l,j);
        return j;
    }

    private void swap(int[] arr, int j, int i) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}
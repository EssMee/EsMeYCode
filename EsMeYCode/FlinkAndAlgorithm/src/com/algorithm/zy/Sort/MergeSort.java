package com.algorithm.zy.Sort;

import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int[] test = {5, 4, 3, 2, 1};
        new MergeSort().sort(test);
        for (int i : test) {
            System.out.print(i + " ");
        }
    }
    public void sort(int[] arr) {
        int n = arr.length;
        mergesort(arr, 0, n - 1);
    }


    /*对[l,r]区间的元素一分为2，递归地进行排序，排序完成之后进行[l,mid],[mid+1,r]两个有序数组的归并操作。*/
    private void mergesort(int[] arr, int l, int r) {
        if (l >= r) return;
        int mid = (r + l) / 2;
        mergesort(arr, l, mid);
        mergesort(arr, mid + 1, r);
        __merge(arr, l, mid, r);
    }

    /*前一个有序数组是[l,mid] 后一个有序数组是[mid+1,r]
    * 注意临时数组和arr有l的偏移量.
    * */
    private void __merge(int[] arr, int l, int mid, int r) {
        int[] temp = Arrays.copyOfRange(arr,l,r+1);
        int i = l;
        int j = mid + 1;
        for (int k = l; k <= r; k++) {
            if (i > mid) {
                arr[k] = temp[j - l];
                j++;
            } else if (j > r) {
                arr[k] = temp[i - l];
                i++;
            } else if (temp[i - l] < temp[j - l]) {
                arr[k] = temp[i - l];
                i++;
            } else {
                arr[k] = temp[j - l];
                j++;
            }

        }
    }
}

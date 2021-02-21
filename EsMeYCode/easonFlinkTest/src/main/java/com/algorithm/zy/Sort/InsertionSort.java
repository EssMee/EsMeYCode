package com.algorithm.zy.Sort;

/*后一个跟前一个比到头，如果小的话，跟前一个交换位置。*/
public class InsertionSort {
    public static void main(String[] args) {
        int[] arr = {1,6,2,3,8,5,7,4};
            sort(arr);
            for (int i : arr) {
                System.out.print(i + ",");
            }
    }

    public static void sort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr, j, j - 1);
                }
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

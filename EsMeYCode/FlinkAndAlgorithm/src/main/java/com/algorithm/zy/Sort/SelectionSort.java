package com.algorithm.zy.Sort;

import java.util.Arrays;

//从第一位开始，内层循环找到最小值的“索引”，与外层的i交换位置。
public class SelectionSort {
    public static void main(String[] args) {
        int[] arr = {1,6,2,3,8,5,7,4};
//        sort(arr);
//        for (int i : arr) {
//            System.out.print(i + ",");
//        }
        Arrays.sort(arr);
                for (int i : arr) {
            System.out.print(i + ",");
        }
    }

/*
     arr[]: 1,6,2,3,8,5,7,4
     1st: 1,6,2,3,8,5,7,4
     2nd: 1,2,6,3,8,5,7,4
*/
    public static void sort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i+1; j < arr.length ; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            swap(arr, i, minIndex);
        }
    }
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

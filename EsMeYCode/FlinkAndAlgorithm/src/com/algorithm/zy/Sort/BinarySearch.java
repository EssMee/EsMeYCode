package com.algorithm.zy.Sort;

/*
二分查找，只对有序数组生效
*/
public class BinarySearch {
    public BinarySearch(){};
    public static int find(int[] arr, int target) {
        int l = 0;
        int r = arr.length - 1;
        while (l<= r) {
            int mid = l + (r - l) /2 ;
            if (arr[mid] == target) {
                return mid;
            }
            if (arr[mid] > target) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return -1;
    }
    public static int findWithRecrusive(int[] arr, int target) {
        return find2(arr, 0, arr.length - 1 , target);
    }
    private static int find2(int[]arr, int l, int r, int target) {
        if (l > r) {
            return -1;
        }
        int mid = l + (r-l)/2;
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] > target) {
            return find2(arr, l, mid-1,target);
        } else {
            return find2(arr,mid+1,r,target);
        }
    }
}

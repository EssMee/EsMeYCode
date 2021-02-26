package com.LeetCode;

import java.util.Arrays;
/*数组转置*/
/*之前m行n列，现在n行m列，原数组的行是新数组的列。*/
public class T867 {
    public static void main(String[] args) {
        //[[1,4,7],[2,5,8],[3,6,9]]
        int[][] input = {{1,2,3},{4,5,6},{7,8,9}};
        int[][] res = new T867().transpose(input);
        for (int[] re : res) {
            for (int i : re) {
                System.out.print(i + " ");
            }
        }
    }

    public int[][] transpose(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int[][] res = new int[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                res[j][i] = matrix[i][j];
            }
        }
        return res;
    }
}

package LeetCode.editor.cn;
//给你一个非递减的 有序 整数数组，已知这个数组中恰好有一个整数，它的出现次数超过数组元素总数的 25%。
//
// 请你找到并返回这个整数 
//
// 
//
// 示例： 
//
// 
//输入：arr = [1,2,2,6,6,6,6,7,10]
//输出：6
// 
//
// 
//
// 提示： 
//
// 
// 1 <= arr.length <= 10^4 
// 0 <= arr[i] <= 10^5 
// 
// Related Topics 数组 
// 👍 28 👎 0


import java.util.HashMap;
import java.util.Map;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public  int findSpecialInteger(int[] arr) {
        int length = arr.length;
        HashMap<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            if (! map.containsKey(arr[i])) {
                map.put(arr[i], 1);
            }
            else {
                Integer previousCount = map.get(arr[i]);
                previousCount += 1;
                map.replace(arr[i], previousCount);
            }
        }
        int res = 0;
        for (Integer key:map.keySet()
             ) {
            if (map.get(key) > 0.25 * length) {
                res = key;
            }
        }
        System.out.println(map);
        return res;
    }

//    public static void main(String[] args) {
//        System.out.println(findSpecialInteger(new int[]{1, 2, 2, 6, 6, 6, 6, 6, 7, 10}));
//
//    }
}
//leetcode submit region end(Prohibit modification and deletion)

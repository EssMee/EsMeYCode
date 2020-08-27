package LeetCode.editor.cn;
/*
 * @lc app=leetcode.cn id=7 lang=java
 *
 * [7] 整数反转
 */

// @lc code=start
class 整数反转 {
    public static int reverse(int x) {
        int ans = 0;
        while (x != 0) {
            if ((ans * 10) / 10 != ans) {
                ans = 0;
                break;
            }
            ans = ans * 10 + x % 10;
            x = x / 10;
        }
        return ans;
    }
    public static void main(String[] args) {
        int test = 321;
        System.out.println(整数反转.reverse(test));
    }
}
// @lc code=end


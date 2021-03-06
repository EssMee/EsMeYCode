/*数组平衡点
* [-7,1,5,2,-4,3,0] -> 2
* */
public class ArrayBalancePoint {
    public static void main(String[] args) {
        int[] arr = {-7,1,5,2,-4,3,0};
        int[] arr2 = {1,2,-3,6,-7,2,5};
        System.out.println(new ArrayBalancePoint().getBalance(arr2));
    }
    public int getBalance(int[] arr) {
        int sum = getSum(arr);
        if (arr.length == 1) {return arr[0];}
        int left = 0;
        int right = 0;
        for (int i = 1; i < arr.length; i++) {
            left += arr[i - 1];
            right = sum - left - arr[i];
            if (left == right) {
                return arr[i];
            }
        }
        return -1;
    }
    private int getSum(int[] arr) {
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        return sum;
    }
}


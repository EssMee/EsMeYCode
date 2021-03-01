package LeetCode;

/*这一题不需要动数组。
        设置一个previous： 上一轮的较大值；
        liansheng：连胜轮次。
        从index=2的元素开始，每一次比较都是与上一轮的较大值作比较，如果新元素比上一轮的元素大，那么它就是第一次开始连胜，
        并且把previous更新为新元素；
如果新元素更小的话，那么说明previous保持不变，连胜次数+1，大于K的时候就可以直接返回了。并且每一次比较之后都记录下数组的最大值，
把整个数组遍历完，如果都没有到k的话，那么直接返回数组的最大值就可以了。*/
public class T1535Again {
    public static void main(String[] args) {
        int[] test = {2,1,3,5,4,6,7};
        System.out.println(new T1535Again().getWinner(test, 2));
    }
    public int getWinner(int[] arr, int k) {
        int previous = Math.max(arr[0], arr[1]);
        if (k == 1) {
            return previous;
        }
        int liansheng = 1;
        int maxNum = previous;
        for (int i = 2; i < arr.length; i++) {
            int current = arr[i];
            if (previous > current) {
                liansheng += 1;
                if (liansheng == k) {
                    return previous;
                }
            } else {
                previous = current;
                liansheng = 1;
            }
            maxNum = Math.max(previous, current);
        }
        return maxNum;
    }
}

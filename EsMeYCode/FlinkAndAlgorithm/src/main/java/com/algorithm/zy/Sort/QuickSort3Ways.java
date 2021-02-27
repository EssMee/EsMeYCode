package PlayWithAlgo;

public class QuickSort3Ways {
    public static void main(String[] args) {
        int[] arr = {6,5,4,3,2,1};
        sort(arr);
        for (int i : arr) {
            System.out.print(i + ",");
        }
    }
    public  static  void sort(int[] arr) {
        int n = arr.length;
        sort(arr, 0, n - 1);
    }

    /*三路快排，通过lt，i，gt把数组分成<v,=v,>v的三部分，始终维持
      arr[l+1...lt]<v; arr[lt+1..i-1]=v;arr[gt..r]>v的性质。
*/
    private  static void sort(int[] arr, int l, int r) {
        if (l >= r) return;
        // _partition方法返回[l,r]的pivot的索引
        swap( arr, l , (int)(Math.random()*(r-l+1))+l );
        int v = arr[l];
        int lt = l;
        int gt = r+1;
        int i = lt + 1;
        while (i < gt) {
            if (arr[i] < v) {
                swap(arr, i,lt+1);
                i ++;
                lt ++;
            } else if (arr[i] > v) {
                swap(arr, gt-1,i);
                gt --;
            } else {
                i ++;
            }
        }
        swap(arr, l , lt);

        sort(arr,l,lt-1);
        sort(arr,gt,r);

    }
    private  static void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}

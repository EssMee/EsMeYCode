/*
记录数组中每个数组出现的次数，放在bucket数组中，bucket数组的长度为原数组最大值+1。
bucket代表从0到bucket.length-1的值出现的次数:
bucket[0]=0代表0出现了0次；bucket[1]=2代表1出现了2次；bucket[2]=2代表2出现了两次。。。
*/
public class SortCollection {
    public static void main(String[] args) {
        int[] test = {2,2,3,4,8,1,1,5,7,6,};
        int[] test2 = {8,6,2,3,1,5,7,4};
        /*int[] res = new SortCollection().CountingSort(test);*/
        new SortCollection().QuickSort(test2);
        for (int re : test2) {
            System.out.print(re + " ");
        }
    }

/*    第二种快速排序
    返回p, 使得arr[l...p-1] < arr[p] ; arr[p+1...r] > arr[p]*/
    private int __partition2(int[] arr, int l, int r) {
        swap(arr, l, (int)Math.random() * (r -l+1) +l);
        int v = arr[l];
        int i = l+1; /*arr[l+1...i-1] < v*/
        int j = r;  /*arr[j+1...r] > v*/
        while (true) {
            while (i <= r && arr[i] < v) {
                i ++;
            }
            while (j >= l+ 1 &&  arr[j] > v) {
                j --;
            }
            if (i > j) {
                break;
            }
            swap(arr, i, j);
            i ++;
            j --;
        }
        swap(arr, l, j );
        return j;
    }
    /*快速排序 O(n*log n)*/
    public void QuickSort(int[] arr) {
        int n = arr.length;
        QuickSort(arr,0,n-1);
    }

    /*在l...r之间找一个标定点，使得arr[l...p-1]<arr[p]; arr[p+1...r] > arr[p];
    然后对arr[l..p-1]以及arr[p+1...r]再使用快速排序，直到l>=r为止。*/
    private void QuickSort(int[] arr, int l, int r) {
        if (l >=r ) return;
        int p = __partition2(arr,l,r);
        QuickSort(arr,l,p-1);
        QuickSort(arr,p+1,r);
    }

    private int __partition(int[] arr, int l, int r) {
        swap(arr, l, (int)Math.random() * (r -l+1) +l);
        int v = arr[l];
        int j = l; // arr[l+1...j] < v; arr[j+1...i-1] > v
        /*保证初始条件下两个区间都不存在*/
        for (int i = l + 1; i <= r ; i++) {
            if (arr[i] < v) {
                j ++;
                swap(arr,i,j);
            }
        }
        swap(arr,l,j);
        return j;
    }

    /*归并排序 O(n*log n)*/
    public void MergeSort(int[] arr) {
        int n = arr.length;
        MergeSort(arr,0, n- 1);
    }

    private void MergeSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int mid = (r + l ) / 2;
        MergeSort(arr,l,mid);
        MergeSort(arr,mid+1,r);
        merge(arr,l,mid,r);
    }
    /*[l...mid]和[mid+1...r]是两个已经排序好的子数组,我现在要进行归并。
    * 注意现在传入的arr是两个有序子数组的union，与原arr不同
    * */
    private void merge(int[] arr, int l, int mid, int r) {
        int[] aux = new int[arr.length];
        for (int i = l; i <=r ; i++) {
            aux[i - l] = arr[i];
        }
        int i = l;
        int j = mid + 1;
        for (int k = l; k <= r; k++) {
            if (i > mid) {
                arr[k] = aux[j-l];
                j ++;
            }
            else if (j > r) {
                arr[k] = aux[i - l] ;
                i ++;
            }
            else if (aux[i - l] < aux[j - l]) {
                arr[k] = aux[i-l];
                i ++;
            } else {
                arr[k] = aux[j -l];
                j ++;
            }
        }
    }


    /*计数排序 */
    public int[] CountingSort(int[] arr) {
        /*计数排序的思路无非就是我想想啊 是咋样的呢*/
        int maxNum = getMaxNum(arr);
        System.out.println("Max: " + maxNum);
        int[] bucket = new int[maxNum + 1];
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int val = arr[i];
            bucket[val] += 1;
        }
        int sortedIndex = 0;
        for (int i = 0; i < bucket.length; i++) {
            while (bucket[i] > 0) {
                res[sortedIndex] = i;
                sortedIndex ++;
                bucket[i] --;
            }
        }
        return res;
    }

    /*插入排序 O(n2)*/
    public void InsertSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            for (int j = i; j > 0 ; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(arr,j, j - 1);
                }
            }
        }
    }
    /*选择排序 O(n2)*/
    public void SelectSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i ++) {
            int min = i;
            for (int j = i + 1; j < n; j ++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            swap(arr, i, min);
        }
    }
    /*冒泡排序 O(n2)*/
    public void BubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[i]) {
                    swap(arr, i, j);
                }
            }
        }
    }

    private void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    private int getMaxNum(int[] arr) {
        int max = arr[0];
        for (int a : arr) {
            if (a >= max) {
                max = a;
            }
        }
        return max;
    }

}

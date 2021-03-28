package msb.Synchro;

import java.util.concurrent.*;

/*尝试使用fork join框架求和
其实就是map/reduce*/
public class ForkJoinCount extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 2;
    private final int start;
    private final int end;
    public ForkJoinCount(int start, int end) {
        this.start = start;
        this.end = end;
    }


    @Override
    protected Integer compute() {
        int sum = 0;
        if (end - start <= THRESHOLD) {
            for (int i = start ; i <= end; i++) {
                sum += i;
            }
        } else {
            int middle = (end + start ) /2 ;
            ForkJoinCount leftCount = new ForkJoinCount(start, middle);
            ForkJoinCount rightCount = new ForkJoinCount(middle + 1, end);
            leftCount.fork();
            rightCount.fork();
            int leftRes = leftCount.join();
            int rightRes = rightCount.join();
            sum = leftRes + rightRes;
        }
        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinCount task = new ForkJoinCount(1,5);
        Future<Integer> result = forkJoinPool.submit(task);
        try {
            System.out.println(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

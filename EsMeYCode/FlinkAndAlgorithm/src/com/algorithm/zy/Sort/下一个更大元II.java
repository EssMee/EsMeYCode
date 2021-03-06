class Solution {
    public int[] nextGreaterElements(int[] nums) {
                int n = nums.length;
        int[] temp = new int[2 * n];
        for (int i = 0; i < n; i++) {
            temp[i] = nums[i];
            temp[i + n] = nums[i];
        }
        int max = getMax(nums);
        for (int i = 0; i < n; i++) {
            if (nums[i] < max) {
                for (int j = i + 1; j < temp.length; j++) {
                    if (temp[j] > nums[i]) {
                        nums[i] = temp[j];
                        break;
                    }
                }
            } else if (nums[i] == max) {
                nums[i] = -1;
                continue;
            }
        }
        return nums;
    }
    
        private int getMax(int[] arr) {
        int max = 0;
        if (arr.length > 0) {
            max = arr[0];
            for(int i : arr) {
                if (i > max) {
                    max = i;
                }
            }
        }
        return max;
    }
}

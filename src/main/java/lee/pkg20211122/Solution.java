package lee.pkg20211122;

public class Solution {

    // final only protect the pointer will not be changed
    private final int[] origin;
    private int[] nums;

    public Solution(int[] nums) {
        this.origin = nums;
        this.nums = nums.clone();
    }

    public int[] reset() {
        return origin;
    }

    public int[] shuffle() {
        for (int i = 0; i < nums.length; i++) {
            // 算一个比i大的随机位置
            // 也可以算一个比i小的随机位置
//            对于下标为 00 位置，从 [0, n - 1][0,n−1] 随机一个位置进行交换，共有 nn 种选择；下标为 11 的位置，
//            从 [1, n - 1][1,n−1] 随机一个位置进行交换，共有 n - 1n−1 种选择 ... 且每个位置的随机位置交换过程相互独立。

//            a pseudorandom double greater than or equal to 0.0 and less than 1.0.
//            [0,1)
            int rand = (int) (Math.random() * (nums.length - i) + i);
            swap(nums, i, rand);
        }
        return nums;
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}

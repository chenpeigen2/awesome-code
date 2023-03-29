package lee.pkg20220428;

import java.util.Arrays;

public class Solution {

    private int[] nums;

    //    https://leetcode-cn.com/problems/sort-array-by-parity/
    public int[] sortArrayByParity(int[] nums) {
        this.nums = nums;
        int i = 0;
        int j = nums.length - 1;
        while (i < j) {
            while (nums[i] % 2 == 0) {
                i++;
            }
            while (nums[j] % 2 != 0) {
                j--;
            }
            if (i < j) {
                swap(i, j);
            }
        }
        return nums;
    }

    // excellent idea 2分法nb
    public int[] sortArrayByParity1(int[] nums) {
        int n = nums.length;
        for (int i = 0, j = n - 1; i < j; i++) {
            if (nums[i] % 2 == 1) {
                int c = nums[j];
                nums[j--] = nums[i];
                nums[i--] = c; //Reset In case it turned nothing
            }
        }
        return nums;
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[] arr = new int[]{3, 1, 2, 4};
        var ans = app.sortArrayByParity1(arr);
        Arrays.stream(ans).forEach(System.out::println);
    }

    public void swap(int i, int j) {
        int tmp = nums[j];
        nums[j] = nums[i];
        nums[i] = tmp;
    }
}

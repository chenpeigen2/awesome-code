package lee.pkg20221127;

public class Solution {
    //    https://leetcode.cn/problems/check-if-array-is-sorted-and-rotated/
    public boolean check(int[] nums) {

        int a = -1;

        int len = nums.length;
        for (int i = 1; i < len; i++) {
            if (nums[i] < nums[i - 1]) {
                a = i - 1;
                break;
            }
        }
        if (a == -1) return true;

        int b = nums[a + 1];

        for (int i = a + 1; i < len; i++) {
            if (nums[i] < b) return false;
            if (nums[i] > nums[0]) return false;
            b = nums[i];
        }

        return true;

    }

    public boolean check1(int[] nums) {
        int n = nums.length, x = 0;
        for (int i = 1; i < n; ++i) {
            if (nums[i] < nums[i - 1]) {
                x = i;
                break;
            }
        }
        if (x == 0) {
            return true;
        }
        for (int i = x + 1; i < n; ++i) {
            if (nums[i] < nums[i - 1]) {
                return false;
            }
        }
        return nums[0] >= nums[n - 1];
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, 3, 2};
        var app = new Solution();
        var ans = app.check(arr);
        System.out.println(ans);
    }


}

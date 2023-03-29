package lee.pkg20220214;

public class Solution {
//    https://leetcode-cn.com/problems/single-element-in-a-sorted-array/solution/
    // if the array is a set ,then it must be first be 2
    // second be 3
    public int singleNonDuplicate(int[] nums) {
        int l = 0, r = nums.length - 1;
        int n = nums.length;
        while (l < r) {
            int mid = (l + r) >> 1;
            if (mid % 2 == 0) {
                //ccaab or bccaa
                if (mid + 1 < n && nums[mid] == nums[mid + 1]) l = mid + 1;
                else r = mid;
            } else {
                // cbb or bbc
                if (mid - 1 >= 0 && nums[mid - 1] == nums[mid]) l = mid + 1;
                else r = mid;
            }
        }
        return nums[l];
    }
}

package lee.pkg20230323;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public List<Boolean> checkArithmeticSubarrays(int[] nums, int[] l, int[] r) {
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < l.length; i++) {
            // copy to a new array
            int[] arr = new int[r[i] - l[i] + 1];
            for (int j = 0; j < arr.length; j++) {
                arr[j] = nums[j + l[i]];
            }
            // judge is valid?
            list.add(isValid(arr));
        }
        return list;
    }

    // judge is valid
    public boolean isValid(int[] nums) {
        // special case
        if (nums.length < 2) {
            return false;
        }
        // sort first
        Arrays.sort(nums);
        int diff = nums[1] - nums[0];
        for (int i = 2; i < nums.length; i++) {
            if (nums[i] - nums[i - 1] != diff) {
                return false;
            }
        }
        return true;
    }
}
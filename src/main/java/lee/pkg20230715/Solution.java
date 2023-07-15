package lee.pkg20230715;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/4sum/
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        int n = nums.length;

        List<List<Integer>> ans = new ArrayList<>();

        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            if (nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target) break;
            if (nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] < target) continue;
            int sum = target - nums[i];
            for (int j = i + 1; j < n - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                if (nums[j] + nums[j + 1] + nums[j + 2] > sum) break;
                if (nums[j] + nums[n - 1] + nums[n - 2] < sum) continue;

                int twosum = sum - nums[j];
                int three = j + 1;
                int four = n - 1;
                while (three < four) {
                    int ss = nums[three] + nums[four];
                    if (ss == twosum) {
                        List<Integer> list = new ArrayList<>();
                        list.add(nums[i]);
                        list.add(nums[j]);
                        list.add(nums[three]);
                        list.add(nums[four]);
                        ans.add(list);
                    }
                    if (nums[three] + nums[four] < twosum) {
                        three++;
                        while (three < four && nums[three - 1] == nums[three]) {
                            three++;
                        }
                    } else {
                        four--;
                        while (three < four && nums[four] == nums[four + 1]) {
                            four--;
                        }
                    }
                }
            }
        }
        return ans;
    }
}

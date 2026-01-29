package 面试题目;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class 双指针 {

    // https://leetcode.cn/problems/move-zeroes/submissions/?envType=study-plan-v2&envId=top-100-liked
    public void moveZeroes(int[] nums) {
        if (nums == null) {
            return;
        }
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[i];
                nums[i] = nums[j];
                nums[j] = temp;
                j++;
            }
        }
    }

    // https://leetcode.cn/problems/container-with-most-water/submissions/608751692/?envType=study-plan-v2&envId=top-100-liked
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int res = 0;

        while (left < right) {
            int temp = (right - left) * Math.max(height[left], height[right]);
            res = Math.max(res, temp);
            if (height[left] > height[right]) {
                right--;
            } else {
                left++;
            }
        }
        return res;
    }


    // https://leetcode.cn/problems/3sum/description/?envType=study-plan-v2&envId=top-100-liked
    public List<List<Integer>> threeSum(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        for (int first = 0; first < n; first++) {
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            int third = n - 1;
            int target = -nums[first];
            for (int second = first + 1; second < third; second++) {
                // 需要和上一次枚举的数不相同
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }
                // 需要保证 b 的指针在 c 的指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    --third;
                }
                if (second == third) {
                    break;
                }
                if (nums[second] + nums[third] == target) {
                    List<Integer> list = new ArrayList<>();
                    list.add(nums[first]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    ans.add(list);
                }
            }
        }
        return ans;
    }

}
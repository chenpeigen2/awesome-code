package 面试题目.day2;

import 面试题目.DoubleCheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class 双指针 {

    // https://leetcode.cn/problems/move-zeroes/submissions/?envType=study-plan-v2&envId=top-100-liked
    public void moveZeroes(int[] nums) {
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
    /**
     * 计算容器能容纳的最大水量（盛最多水的容器）
     * 使用双指针法：左指针指向数组起始，右指针指向数组末尾。
     * 每次移动较短边的指针，因为移动较长边无法增加容量（宽度减小，高度受限于短边）。
     *
     * @param height 表示每个位置柱子高度的整数数组
     * @return 返回能容纳的最大水量
     */
    public int maxArea(int[] height) {
        // 初始化左指针在数组最左侧
        int left = 0;
        // 初始化右指针在数组最右侧
        int right = height.length - 1;
        // 用于记录当前找到的最大面积
        int res = 0;
        // 当左指针小于右指针时继续循环
        while (left < right) {
            // 计算当前左右指针围成的容器面积：宽度 * 较短边的高度
            int temp = (right - left) * Math.min(height[left], height[right]);
            // 更新最大面积
            res = Math.max(res, temp);
            // 移动较短边的指针，尝试寻找更高的边以增大面积
            if (height[left] > height[right]) {
                // 右边较短，移动右指针向左
                right--;
            } else {
                // 左边较短或相等，移动左指针向右
                left++;
            }
        }
        // 返回计算得到的最大水量
        return res;
    }


    // https://leetcode.cn/problems/3sum/description/?envType=study-plan-v2&envId=top-100-liked
    /**
     * 找出数组中所有和为 0 的不重复三元组（三数之和）
     * 使用排序 + 双指针法：
     * 1. 首先对数组进行排序。
     * 2. 遍历数组，固定第一个数 nums[first]。
     * 3. 使用双指针 second 和 third 分别指向 first+1 和数组末尾，寻找满足 nums[second] + nums[third] == -nums[first] 的组合。
     * 4. 通过跳过重复元素来确保结果不包含重复的三元组。
     *
     * @param nums 输入的整数数组
     * @return 包含所有不重复三元组的列表，每个三元组的和为 0
     */
    @DoubleCheck
    public List<List<Integer>> threeSum(int[] nums) {
        // 获取数组长度
        int n = nums.length;
        // 对数组进行排序，以便使用双指针法和去重
        Arrays.sort(nums);
        // 存储结果的列表
        List<List<Integer>> ans = new ArrayList<>();
        // 遍历数组，固定第一个数
        for (int first = 0; first < n; first++) {
            // 跳过重复的第一个数，避免重复的三元组
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            // 初始化右指针 third 指向数组末尾
            int third = n - 1;
            // 目标值：我们需要找到两个数，它们的和等于 -nums[first]
            int target = -nums[first];
            // 遍历第二个数，从 first + 1 开始
            for (int second = first + 1; second < third; second++) {
                // 跳过重复的第二个数，避免重复的三元组
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }
                // 移动右指针 third，直到 nums[second] + nums[third] <= target
                // 保证 second 指针在 third 指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    --third;
                }
                // 如果指针重合，说明后续没有满足条件的组合了，跳出内层循环
                if (second == third) {
                    break;
                }
                // 如果找到满足条件的三元组
                if (nums[second] + nums[third] == target) {
                    List<Integer> list = new ArrayList<>();
                    list.add(nums[first]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    ans.add(list);
                }
            }
        }
        // 返回所有找到的三元组
        return ans;
    }

}
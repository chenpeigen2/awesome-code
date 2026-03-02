package 面试题目.day2;

import java.util.*;

public class 哈希 {

    // https://leetcode.cn/problems/two-sum/submissions/608692905/?envType=study-plan-v2&envId=top-100-liked
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    // https://leetcode.cn/problems/group-anagrams/submissions/608691781/?envType=study-plan-v2&envId=top-100-liked
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] arr = str.toCharArray();
            Arrays.sort(arr);
            String s = new String(arr);
            List<String> list = map.getOrDefault(s, new ArrayList<>());
            list.add(str);
            map.put(s, list);
        }
        return new ArrayList<>(map.values());
    }

    // https://leetcode.cn/problems/longest-consecutive-sequence/submissions/608694795/?envType=study-plan-v2&envId=top-100-liked
    /**
     * 计算数组中最长连续序列的长度。
     * 
     * 算法思路：
     * 1. 首先对数组进行排序，使连续的数字相邻。
     * 2. 处理边界情况：如果数组为空，直接返回 0。
     * 3. 初始化结果 result 为 1（至少有一个元素时长度为 1），当前连续计数 count 为 1，前一个数字 prev 为数组第一个元素。
     * 4. 从第二个元素开始遍历数组：
     *    - 如果当前数字等于前一个数字加 1 (prev + 1 == nums[i])，说明是连续的，count 自增。
     *    - 如果当前数字等于前一个数字 (nums[i] == nums[i-1])，说明是重复元素，跳过本次循环，不重置 count。
     *    - 否则，说明连续性中断，将 count 重置为 1。
     * 5. 在每次循环中更新 prev 为当前数字，并更新最大长度 result。
     * 6. 返回最终计算出的最长连续序列长度。
     * 
     * 时间复杂度：O(N log N)，主要消耗在排序上。
     * 空间复杂度：O(1) 或 O(log N)，取决于排序算法的实现。
     * 
     * @param nums 输入的整数数组
     * @return 最长连续序列的长度
     */
    public int longestConsecutive(int[] nums) {
        // 对数组进行排序，以便后续判断连续性
        Arrays.sort(nums);
        
        // 边界条件：如果数组为空，最长连续序列长度为 0
        if (nums.length == 0) return 0;
        
        // 初始化结果为 1，因为非空数组至少有一个元素
        int result = 1;
        
        // 遍历数组，i 从 1 开始
        // count: 当前连续序列的长度
        // prev: 记录上一个处理的数字，用于判断是否连续
        for (int i = 1, count = 1, prev = nums[0]; i < nums.length; i++) {
            if (prev + 1 == nums[i]) {
                // 当前数字与前一个数字连续，计数加 1
                count++;
            } else if (nums[i] == nums[i - 1]) {
                // 当前数字与前一个数字相同（重复元素），跳过，不重置计数
                continue;
            } else {
                // 连续性中断，重置当前计数为 1
                count = 1;
            }
            
            // 更新前一个数字为当前数字
            prev = nums[i];
            
            // 更新全局最大长度
            result = Math.max(result, count);
        }

        return result;
    }

}

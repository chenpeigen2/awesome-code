package 面试题目;

import java.util.*;

public class 哈希 {

    // https://leetcode.cn/problems/two-sum/submissions/608692905/?envType=study-plan-v2&envId=top-100-liked
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{target - nums[i], nums[i]};
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
    public int longestConsecutive(int[] nums) {
        Arrays.sort(nums);
        if (nums.length == 0) return 0;
        int ret = 1;
        for (int i = 1, count = 1, prev = nums[0]; i < nums.length; i++) {
            if (nums[i] == prev + 1) {
                count++;
            } else if (nums[i] == nums[i - 1]) {
                continue;
            } else {
                count = 1;
            }
            prev = nums[i];
            ret = Math.max(ret, count);
        }
        return ret;
    }

}

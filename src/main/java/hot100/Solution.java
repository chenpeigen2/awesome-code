package hot100;

import java.util.*;

public class Solution {
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


    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
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

    public int sumNumbers(TreeNode root) {
        if (root == null) return 0;
        dfs_sumNumbers(0, root);
        return sumNumbers_sum;
    }

    int sumNumbers_sum = 0;

    void dfs_sumNumbers(int level_sum, TreeNode node) {
        level_sum = level_sum * 10 + node.val;
        if (node.left == null && node.right == null) {
            sumNumbers_sum += level_sum;
        }
        if (node.left != null) dfs_sumNumbers(level_sum, node.left);
        if (node.right != null) dfs_sumNumbers(level_sum, node.right);
    }

    //    https://leetcode.cn/problems/move-zeroes/?envType=study-plan-v2&envId=top-100-liked
    public void moveZeroes(int[] nums) {
        if (nums == null) {
            return;
        }
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[i];

                nums[i] = nums[j];

                nums[j++] = temp;
            }
        }
    }

    public int removeElement(int[] nums, int val) {
//        int l = 0;
//        int r = nums.length;
//
//
//        while (l < r) {
//            if (nums[l] == val) {
//                nums[l] = nums[r - 1];
//                r--;
//            } else {
//                l++;
//            }
//        }
//        return l;

        int left = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] != val) {
                nums[left] = nums[right];
                left++;
            }
        }
        return left;
    }

    //    https://leetcode.cn/problems/remove-duplicates-from-sorted-array/?envType=study-plan-v2&envId=top-interview-150
    public int removeDuplicates(int[] nums) {
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[left] == nums[right]) continue;
            nums[++left] = nums[right];
        }

        return left + 1;
    }

}

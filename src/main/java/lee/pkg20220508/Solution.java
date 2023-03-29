package lee.pkg20220508;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
    //    https://leetcode-cn.com/problems/find-all-duplicates-in-an-array/

    //    https://leetcode-cn.com/problems/find-all-duplicates-in-an-array/solution/by-fuxuemingzhu-dko5/
    public List<Integer> findDuplicates(int[] nums) {
        Set<Integer> s = new HashSet<>();
        List<Integer> ans = new ArrayList<>();
        for (int i : nums) {
            if (s.contains(i)) {
                ans.add(i);
            }
            s.add(i);
        }
        return ans;
    }

    public List<Integer> findDuplicates1(int[] nums) {
        List<Integer> ret = new ArrayList<>();
        for (int i : nums) {
            if (nums[Math.abs(i) - 1] < 0) ret.add(Math.abs(i));
            else nums[Math.abs(i) - 1] *= -1;
        }
        return ret;
    }

    public List<Integer> findDuplicates2(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; ++i) {
            while (nums[i] != nums[nums[i] - 1]) {
                swap(nums, i, nums[i] - 1);
            }
        }
        List<Integer> ans = new ArrayList<Integer>();
        for (int i = 0; i < n; ++i) {
            if (nums[i] - 1 != i) {
                ans.add(nums[i]);
            }
        }
        return ans;
    }

    public void swap(int[] nums, int index1, int index2) {
        int temp = nums[index1];
        nums[index1] = nums[index2];
        nums[index2] = temp;
    }
}

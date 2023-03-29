package lee.pkg20211120;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    public int findLHS(int[] nums) {
        Map<Integer, Integer> m = new HashMap<>();
        for (int i : nums) {
            m.put(i, m.getOrDefault(i, 0) + 1);
        }

        int ans = 0;
//        ，它可以通过删除一些元素或不删除元素、且不改变其余元素的顺序而得到。
        for (int i : nums) {
            if (m.containsKey(i - 1)) {
                ans = Math.max(ans, m.get(i - 1) + m.get(i));
            }
        }

        return ans;
    }


    public int findLHS1(int[] nums) {
        Arrays.sort(nums);
        int n = nums.length, ans = 0;
        for (int j = 0, i = 0; j < n; j++) {
            while (nums[j] - nums[i] > 1) i++;
            if (nums[j] - nums[i] == 1)
                ans = Math.max(ans, j - i + 1);
        }
        return ans;
    }

}

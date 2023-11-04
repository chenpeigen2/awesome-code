package lee.pkg20231104;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/maximum-xor-of-two-numbers-in-an-array/?envType=daily-question&envId=2023-11-04
    public int findMaximumXOR(int[] nums) {
        int ret = 0;
        int mask = 0;
        for (int i = 30; i >= 0; i--) {
            mask = mask | (1 << i);
            Set<Integer> set = new HashSet<>();
            for (int num : nums) {
                set.add(num & mask);
            }
            int temp = ret | (1 << i);
            for (Integer prefix : set) {
                if (set.contains(temp ^ prefix)) {
                    ret = temp;
                    break;
                }
            }
        }

        return ret;
    }
}

package lee.pkg20220928;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/get-kth-magic-number-lcci/
    public int getKthMagicNumber(int k) {
        int[] nums = new int[]{3, 5, 7};
        PriorityQueue<Long> q = new PriorityQueue<>();
        Set<Long> set = new HashSet<>();
        q.add(1L);
        set.add(1L);
        while (!q.isEmpty()) {
            long t = q.poll();
            if (--k == 0) return (int) t;
            for (int x : nums) {
                if (!set.contains(x * t)) {
                    q.offer(x * t);
                    set.add(x * t);
                }
            }
        }
        return -1;
    }
}

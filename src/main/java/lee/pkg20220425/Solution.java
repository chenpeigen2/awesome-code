package lee.pkg20220425;

import java.util.*;

public class Solution {
    Random random = new Random();
    Map<Integer, List<Integer>> map = new HashMap<>();

    public Solution(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            List<Integer> l = map.computeIfAbsent(nums[i], k -> new ArrayList<>());
            l.add(i);
        }
    }

    public int pick(int target) {
        List<Integer> l = map.get(target);
        return l.get(random.nextInt(l.size()));
    }
}

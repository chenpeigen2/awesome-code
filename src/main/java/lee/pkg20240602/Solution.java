package lee.pkg20240602;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/distribute-candies/?envType=daily-question&envId=2024-06-02
    public int distributeCandies(int[] candyType) {
        Set<Integer> set = new HashSet<>();
        for (int i : candyType) {
            set.add(i);
        }
        int sz = candyType.length;
        int a = sz / 2;
        return Math.min(a, set.size());
    }
}

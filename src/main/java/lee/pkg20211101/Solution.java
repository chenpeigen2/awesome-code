package lee.pkg20211101;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    public int distributeCandies(int[] candyType) {
        Set<Integer> s = new HashSet<>();
        for (int i : candyType) {
            s.add(i);
        }
        int expect = candyType.length / 2;
        return Math.min(expect, s.size());
    }
}

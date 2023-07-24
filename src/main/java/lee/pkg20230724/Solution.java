package lee.pkg20230724;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/jewels-and-stones/
    public int numJewelsInStones(String jewels, String stones) {
        int ans = 0;
        Set<Character> set = new HashSet<>();
        for (char jewel : jewels.toCharArray()) {
            set.add(jewel);
        }
        for (char stone : stones.toCharArray()) {
            if (set.contains(stone)) ans++;
        }
        return ans;
    }
}

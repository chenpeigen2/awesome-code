package lee.pkg20220927;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/check-permutation-lcci/
    public boolean CheckPermutation(String s1, String s2) {
        char[] c1 = s1.toCharArray();
        Arrays.sort(c1);
        char[] c2 = s2.toCharArray();
        Arrays.sort(c2);
        return Arrays.compare(c1, c2) == 0;
    }
}

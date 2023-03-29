package lee.pkg20220824;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/make-two-arrays-equal-by-reversing-sub-arrays/
    public boolean canBeEqual(int[] target, int[] arr) {
        Arrays.sort(target);
        Arrays.sort(arr);
        for (int i = 0; i < target.length; i++) {
            if (arr[i] != target[i]) return false;
        }
        return true;
    }
}

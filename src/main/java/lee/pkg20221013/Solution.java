package lee.pkg20221013;

public class Solution {
    //    https://leetcode.cn/problems/max-chunks-to-make-sorted/
    public int maxChunksToSorted(int[] arr) {
        int m = 0, res = 0;
        for (int i = 0; i < arr.length; i++) {
            m = Math.max(m, arr[i]);
            if (m == i) {
                res++;
            }
        }
        return res;
    }
}

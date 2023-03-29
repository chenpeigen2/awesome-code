package lee.pkg20221221;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/maximum-score-from-removing-stones/
//    为了使分数最大化，每次将最大的两个数进行减少，直到有两堆为0。
//    (0, 1, 1)
    public int maximumScore(int a, int b, int c) {
        int[] arr = new int[]{a, b, c};
        Arrays.sort(arr);
        int ans = 0;
        while (arr[0] != 0 || arr[1] != 0) {
            ans++;
            arr[2]--;
            arr[1]--;
            Arrays.sort(arr);
        }
        return ans;
    }
}

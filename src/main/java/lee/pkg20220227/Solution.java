package lee.pkg20220227;

import java.util.Arrays;

public class Solution {
    //    https://leetcode-cn.com/problems/optimal-division/


//    也可以这样理解，分子固定第一个数，分母越小越好，怎样才会越小越好，因为题目限制了数字诠释正整数，肯定越除越小，
//    那就把从第二个数开始到结尾的所有数全部作为分母，这样的时候最小。总之这个题有点怪怪的，分析出来后都有点不自信了
    public String optimalDivision(int[] nums) {
        int n = nums.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(nums[i]);
            if (i + 1 < n) sb.append("/");
        }
        if (n > 2) {
            sb.insert(sb.indexOf("/") + 1, "(");
            sb.append(")");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[] arr = new int[]{1000, 100, 10, 2};
        var ans = app.optimalDivision(arr);
        System.out.println(ans);
    }
}

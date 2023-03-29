package lee.pkg20220908;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/beautiful-arrangement-ii/
    public int[] constructArray(int n, int k) {
        int[] ans = new int[n];
        int idx = 0;
        for (int i = 1; i < n - k; i++) {
            ans[idx] = i;
            idx++;
        }

        for (int i = n - k, j = n; i <= j; i++, j--) {
            ans[idx] = i;
            idx++;
            if (i != j) {
                ans[idx] = j;
                idx++;
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
       var ans =  app.constructArray(12, 5);
        Arrays.stream(ans).forEach(x -> System.out.println(x));
    }
}

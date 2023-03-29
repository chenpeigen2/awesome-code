package lee.pkg20220306;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode-cn.com/problems/find-good-days-to-rob-the-bank/submissions/
    public List<Integer> goodDaysToRobBank(int[] security, int time) {
        int n = security.length;
        int[] g = new int[n];
        for (int i = 1; i < n; i++) {
            if (security[i] == security[i - 1]) continue;
            g[i] = security[i] > security[i - 1] ? 1 : -1;
        }

        // 为了记录一个变化，特意浪费了一个空间，md
        int[] a = new int[n], b = new int[n];
        // 递增的描述
        for (int i = 1; i < n; i++) a[i] = a[i - 1] + (g[i] == 1 ? 1 : 0);
        // 递减的描述
        for (int i = 1; i < n; i++) b[i] = b[i - 1] + (g[i] == -1 ? 1 : 0);

//        且满足 (i - time, i](i−time,i] 范围前缀 11 数量为 00，(i, i + time](i,i+time] 范围前缀 -1−1 数量为 00。
        List<Integer> ans = new ArrayList<>();
        for (int i = time; i < n - time; i++) {
            // a[3] - a[1]  == > a[2] and a[3] 所累计的变化 => security的第三个位置和第二个位置
            int c1 = a[i] - a[i - time];  // 递增个数为0
            int c2 = b[i + time] - b[i];  // 递减个数为0
            //... i+1 i+2 i+3       ...i+1   => i+2 i+3

//            security[0] >= security[1] >= security[2] <= security[3] <= security[4] 。
            if (c1 == 0 && c2 == 0) ans.add(i);
        }

        return ans;
    }

    public static void main(String[] args) {
        var security = new int[]{5, 2, 3, 3, 5, 6, 2};
        int time = 2;
        new Solution().goodDaysToRobBank(security, time);
    }
}

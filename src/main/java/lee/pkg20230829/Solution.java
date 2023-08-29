package lee.pkg20230829;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/binary-trees-with-factors/
//    public int numFactoredBinaryTrees(int[] arr) {
//        final long MOD = (long) 1e9 + 7;
//        int n = arr.length;
//        Set<Integer> s = new HashSet<>();
//        for (int x : arr) {
//            s.add(x);
//        }
//
//        long ans = 0;
//        for (int x : arr) {
//            ans += dfs(x, arr, s);
//        }
//        return (int) (ans % MOD);
//    }
//
//    private long dfs(int val, int[] arr, Set<Integer> s) {
//        long res = 1;
//        for (int x : arr) {
//            if (val % x == 0 && s.contains(val / x)) {
//                res += dfs(x, arr, s) * dfs(val / x, arr, s);
//            }
//        }
//        return res;
//    }

    public int numFactoredBinaryTrees(int[] arr) {
        final long MOD = (long) 1e9 + 7;
        Arrays.sort(arr);
        int n = arr.length;
        Map<Integer, Integer> idx = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            idx.put(arr[i], i);
        }

        long[] memo = new long[n];
        Arrays.fill(memo, -1); // -1 表示没有计算过
        long ans = 0;
        for (int i = 0; i < n; i++) {
            ans += dfs(i, arr, memo, idx);
        }
        return (int) (ans % MOD);
    }

    private long dfs(int i, int[] arr, long[] memo, Map<Integer, Integer> idx) {
        if (memo[i] != -1) // 之前计算过
            return memo[i];
        int val = arr[i];
        long res = 1;
        for (int j = 0; j < i; ++j) { // val 的因子一定比 val 小
            int x = arr[j];
            if (val % x == 0 && idx.containsKey(val / x)) { // 另一个因子 val/x 必须在 arr 中
                res += dfs(j, arr, memo, idx) * dfs(idx.get(val / x), arr, memo, idx);
            }
        }
        return memo[i] = res; // 记忆化
    }
}

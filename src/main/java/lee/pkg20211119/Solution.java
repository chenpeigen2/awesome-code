package lee.pkg20211119;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public int integerReplacement(int n) {
        if (n < 2) {
            return 0;
        }
        int[] arr = new int[n + 1];
        arr[0] = 0;
        arr[1] = 0;
        for (int i = 2; i <= n; i++) {
            if (i % 2 == 0) {
                arr[i] = arr[i / 2] + 1;
            } else {
                arr[i] = Math.min(arr[(i + 1) / 2] + 2, arr[(i - 1) / 2] + 2);
            }
        }
        return arr[n];
    }

    public int integerReplacement1(int n) {
        if (n == 1) {
            return 0;
        }
        if (n % 2 == 0) {
            return integerReplacement1(n / 2) + 1;
        } else {
            return 2 + Math.min(integerReplacement1((n + 1) / 2), integerReplacement1((n - 1) / 2));
        }

    }


    Map<Long, Integer> map = new HashMap<>();

    public int integerReplacement2(int n) {
        return dfs(n * 1L);
    }

    int dfs(long n) {
        if (n == 1) return 0;
        if (map.containsKey(n)) return map.get(n);
        int ans = n % 2 == 0 ? dfs(n / 2) : Math.min(dfs(n + 1), dfs(n - 1));
        map.put(n, ++ans);
        return ans;
    }

    // 给递归加上一些记忆就是记忆化搜索
    Map<Integer, Integer> memo = new HashMap<Integer, Integer>();

    public int integerReplacement3(int n) {
        if (n == 1) {
            return 0;
        }
        if (!memo.containsKey(n)) {
            if (n % 2 == 0) {
                memo.put(n, 1 + integerReplacement3(n / 2));
            } else {
                memo.put(n, 2 + Math.min(integerReplacement3(n / 2), integerReplacement3(n / 2 + 1)));
            }
        }
        return memo.get(n);
    }
}

package lee.pkg20240323;

import java.util.*;

public class Solution {

    //    https://leetcode.cn/problems/count-distinct-numbers-on-board/?envType=daily-question&envId=2024-03-23
    public int distinctIntegers(int n) {
        int[] nums = new int[n + 1];
        nums[n] = 1;
        for (int k = 0; k < n; k++) {
            for (int x = 1; x <= n; x++) {
                if (nums[x] == 0) continue;

                for (int i = 1; i <= n; i++) {
                    if (x % i == 1) {
                        nums[i] = 1;
                    }
                }

            }
        }
        return Arrays.stream(nums).sum();
    }

    // 当 n>1n \gt 1n>1 时，那么经过多次操作后，一定可以将 n−1,n−2,…,2n - 1, n - 2, \ldots, 2n−1,n−2,…,2 依次放到桌面上。
    // 当 n=1n = 1n=1 时，桌面只有一个数字 111。
    public int distinctIntegers1(int n) {
        return n == 1 ? 1 : n - 1;
    }
}

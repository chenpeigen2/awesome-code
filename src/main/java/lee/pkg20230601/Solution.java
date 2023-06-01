package lee.pkg20230601;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/maximum-tastiness-of-candy-basket/
    public int maximumTastiness(int[] price, int k) {
        Arrays.sort(price);
        int left = 0, right = price[price.length - 1] - price[0];
        while (left < right) {
            int mid = (left + right + 1) / 2;
            if (check(price, k, mid)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    // https://leetcode.cn/problems/maximum-tastiness-of-candy-basket/solution/li-he-de-zui-da-tian-mi-du-by-leetcode-s-sq44/
    public boolean check(int[] price, int k, int tastiness) {
        int prev = price[0];
        int cnt = 1;
        for (int p : price) {
            if (p - prev >= tastiness) {
                cnt++;
                prev = p;
            }
        }
        return cnt >= k;
    }
}

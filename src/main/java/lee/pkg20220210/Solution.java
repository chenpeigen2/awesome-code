package lee.pkg20220210;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    利用到的原理：(a, b)与(b, a % b)的最大公约数相同。
    int gcd(int a, int b) { // 欧几里得算法
        return b == 0 ? a : gcd(b, a % b);
    }

    public List<String> simplifiedFractions(int n) {
        List<String> ans = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (gcd(i, j) == 1) ans.add(i + "/" + j);
            }
        }
        return ans;
    }
}


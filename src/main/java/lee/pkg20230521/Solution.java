package lee.pkg20230521;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/o8SXZn/
    public int storeWater(int[] bucket, int[] vat) {
        int n = vat.length;
        int maxK = Arrays.stream(vat).max().getAsInt();
        if (maxK == 0) return 0;
        int res = Integer.MAX_VALUE;
        for (int k = 1; k <= maxK && k < res; k++) {
            int t = 0;

            for (int i = 0; i < n; i++) {
                t += Math.max(0, (vat[i] - 1 + k) / k - bucket[i]); // 需要升级的bucket的容量
            }

            res = Math.min(res, t + k);
        }

        return res;
    }
}

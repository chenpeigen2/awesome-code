package lee.pkg20221115;

import java.util.Arrays;
import java.util.Comparator;

public class Solution {
    //    https://leetcode.cn/problems/maximum-units-on-a-truck/
    public int maximumUnits(int[][] boxTypes, int truckSize) {
        Arrays.sort(boxTypes, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[1] - o1[1];
            }
        });
        int idx = 0;
        int ans = 0;
        while (truckSize > 0 && idx < boxTypes.length) {
            int[] box = boxTypes[idx];
            int len = box[0];
            if (truckSize - len >= 0) {
                truckSize -= len;
                ans += box[0] * box[1];
            } else {
                ans += truckSize * box[1];
                truckSize = 0;
            }
            idx++;
        }

        return ans;
    }


    public int maximumUnits1(int[][] boxTypes, int truckSize) {
        int count = 0;
        int[] ans = new int[1001];
        for (int[] is : boxTypes) {
            ans[is[1]] += is[0];
        }
        for (int i = ans.length - 1; i > 0; i--) {
            if (ans[i] != 0) {
                if (truckSize > ans[i]) {
                    count += ans[i] * i;
                    truckSize -= ans[i];
                } else {
                    return count + truckSize * i;
                }
            }
        }
        return count;
    }
}

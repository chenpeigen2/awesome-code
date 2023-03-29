package lee.pkg20220724;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/distance-between-bus-stops/
    public int distanceBetweenBusStops(int[] distance, int start, int destination) {

        int len = distance.length, i = start, j = start, a = 0, b = 0;
        while (i != destination) {
            a += distance[i];
            if (++i == len) i = 0;
        }

        while (j != destination) {
            if (--j < 0) j = len - 1;
            b += distance[j];
        }
        return Math.min(a, b);

//        int min = Math.min(start, destination);
//        int max = Math.max(start, destination);
//        int a1 = 0;
//        for (int i = min; i < max; i++) {
//            a1 += distance[i];
//        }
//        int sum = Arrays.stream(distance).sum();
//        return Math.min(a1, sum - a1);
    }
}

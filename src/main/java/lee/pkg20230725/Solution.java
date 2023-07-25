package lee.pkg20230725;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Solution {
    //    https://leetcode.cn/problems/minimum-operations-to-halve-array-sum/
    public int halveArray(int[] nums) {
        PriorityQueue<Double> priorityQueue = new PriorityQueue<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o2.compareTo(o1);
            }
        });

        double sum = 0;

        for (int num : nums) {
            sum += num;
            priorityQueue.offer((double) num);
        }

        double expectedSum = 0;
        double splitSum = sum / 2;

        int res = 0;

        while (expectedSum < splitSum) {
            double vvv = priorityQueue.poll();
            double v2 = vvv / 2;
            expectedSum += v2;
            priorityQueue.offer(v2);
            res++;
        }

        return res;

    }
}

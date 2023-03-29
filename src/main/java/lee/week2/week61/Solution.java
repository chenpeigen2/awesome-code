package lee.week2.week61;

import java.util.*;

public class Solution {
    public int countKDifference(int[] nums, int k) {
        int count = 0;
        Arrays.sort(nums);
        Map<Integer, Integer> m = new HashMap<>();
        for (int num : nums) {
            // 最大的那个数的 个数
            int value = m.getOrDefault(num, 0) + 1;
            // 第二个数的那个value的个数
            int tmp = m.getOrDefault(num - k, 0);
            count = count + tmp;
            m.put(num, value);
        }
        return count;
    }

    public int[] findOriginalArray(int[] changed) {
        if (changed.length % 2 != 0) {
            return new int[0];
        }
        int[] arr = new int[changed.length / 2];
        int index = 0;
        Map<Integer, Integer> m = new HashMap<>();
        Arrays.sort(changed);
        for (int v : changed) {
            m.put(v, m.getOrDefault(v, 0) + 1);
        }

        int i = m.getOrDefault(0, 0);
        if (i > 0) {
            if (i % 2 == 0) {
                for (int j = 0; j < i / 2; j++) {
                    arr[index++] = 0;
                }
            } else {
                return new int[0];
            }
        }

        for (; i < changed.length; i++) {
            int v = changed[i];

            if (m.getOrDefault(v, -1) > 0 && index < arr.length) {
                if (v % 2 != 0) {
                    if (m.getOrDefault(v * 2, -1) <= 0) {
                        return new int[0];
                    } else if (m.getOrDefault(v * 2, -1) > 0) {
                        m.put(v, m.get(v) - 1);
                        m.put(v * 2, m.get(v * 2) - 1);
                        arr[index++] = v;
                    }
                } else {
//                    if (m.getOrDefault(v / 2, -1) > 0) {
//                        m.put(v, m.get(v) - 1);
//                        m.put(v / 2, m.get(v / 2) - 1);
//                        arr[index++] = v / 2;
//                    }
                    if (m.getOrDefault(v * 2, -1) > 0) {
                        m.put(v, m.get(v) - 1);
                        m.put(v * 2, m.get(v * 2) - 1);
                        arr[index++] = v;
                    }
                }
            }
        }
        if (index != arr.length) {
            return new int[0];
        }
        return arr;
    }

//    [[2,3,6],[8,9,8],[5,9,7],[8,9,1],[2,9,2],[9,10,6],[7,10,10],[6,7,9],[4,9,7],[2,3,1]]

    public long maxTaxiEarnings(int n, int[][] rides) {
        this.n = n;
        Arrays.sort(rides, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] - o2[0] > 0) {
                    return 1;
                } else if (o1[0] == o2[0]) {
                    return o1[1] - o2[0];
                }
                return -1;
            }
        });
        search(rides, 0, 0);
        return maxvalue;
    }

    long maxvalue = 0;
    long curValue = 0;
    int n;

    public void search(int[][] rides, int index, int lastEndpoint) {
        if (index == rides.length) {
            if (maxvalue < curValue) {
                maxvalue = curValue;
            }
        } else {
            if (canChoose(lastEndpoint, rides[index][0], rides[index][1])) {
                curValue = curValue + rides[index][1] - rides[index][0] + rides[index][2];
                search(rides, index + 1, rides[index][1]);
                curValue = curValue - rides[index][1] + rides[index][0] - rides[index][2];
            }
            search(rides, index + 1, lastEndpoint);
        }

    }

    public boolean canChoose(int lastEndpoint, int startPoint, int endPoint) {
        if (lastEndpoint <= startPoint && startPoint <= n && endPoint <= n) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

//        10
//                [[2,3,6],[8,9,8],[5,9,7],[8,9,1],[2,9,2],[9,10,6],[7,10,10],[6,7,9],[4,9,7],[2,3,1]]

        int[][] rides = new int[][]{
                {2, 3, 6},
                {8, 9, 8},
                {5, 9, 7},
                {8, 9, 1},
                {2, 9, 2},
                {9, 10, 6},
                {7, 10, 10},
                {6, 7, 9},
                {4, 9, 7},
                {2, 3, 1}
        };
        int n = 10;
        var app = new Solution();
        var result = app.maxTaxiEarnings(n, rides);
        System.out.println(result);
//        int[] changed = new int[]{6, 3, 0, 1};
//        var result = new Solution().findOriginalArray(changed);
//        for (int i = 0; i < result.length; i++) {
//            System.out.println(result[i]);
//        }
    }
}

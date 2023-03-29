package lee.pkg20220401;

import java.util.*;

public class Solution {
    //    https://leetcode-cn.com/problems/array-of-doubled-pairs/
//    public boolean canReorderDoubled(int[] arr1) {
//        Integer[] arr = new Integer[arr1.length];
//        for (int i = 0; i < arr1.length; i++) {
//            arr[i] = arr1[i];
//        }
//
//        Arrays.sort(arr, (o1, o2) -> {
//            if (o1 < 0 && o2 < 0) {
//                return o2 - o1;
//            }
//            return o1 - o2;
//        });
//
//        int split = 0;
//        while (split < arr.length && arr[split] < 0) split++;
//
//        int[] flags = new int[arr.length];
//
//        for (int i = 0; i < split; i++) {
//            if (flags[i] == 1) continue;
//            int j = i + 1;
//            if (j >= split) {
//                return false;
//            }
//            while (j < split && flags[j] == 1) j++;
//            while (j < split && 2 * arr[i] != arr[j]) {
//                j++;
//            }
//            if (j >= split || 2 * arr[i] != arr[j]) {
//                return false;
//            } else {
//                flags[j] = 1;
//            }
//        }
//
////        [2,1,2,1,1,1,2,2]
//        for (int i = split; i < arr.length; i++) {
//            if (flags[i] == 1) continue;
//            int j = i + 1;
//            if (j >= arr.length) {
//                System.out.println(11);
//                return false;
//            }
//            while (j < arr.length && flags[j] == 1) j++;
//            while (j < arr.length && 2 * arr[i] != arr[j]) {
//                j++;
//            }
//            if (j >= arr.length || 2 * arr[i] != arr[j]) {
//                System.out.println(22);
//                return false;
//            } else {
//                flags[i] = 1;
//                flags[j] = 1;
//            }
//        }
//
//
//        return true;
//    }

    public boolean canReorderDoubled(int[] arr) {
        Map<Integer, Integer> cnt = new HashMap<>();
        for (int x : arr) {
            cnt.put(x, cnt.getOrDefault(x, 0) + 1);
        }
        // 0 only match the 0
        if (cnt.getOrDefault(0, 0) % 2 != 0) return false;

        List<Integer> vals = new ArrayList<>();
        for (int x : cnt.keySet()) {
            vals.add(x);
        }
        Collections.sort(vals, (a, b) -> Math.abs(a) - Math.abs(b));
        // 80 40 20 10

        for (int x : vals) {
            // do not meet demands
            if (cnt.getOrDefault(2 * x, 0) < cnt.get(x)) return false;

            cnt.put(2 * x, cnt.getOrDefault(2 * x, 0) - cnt.get(x));
        }
        return true;
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[] arr = new int[]{10, 20, 40, 80};
        var ans = app.canReorderDoubled(arr);
        System.out.println(ans);
    }
}

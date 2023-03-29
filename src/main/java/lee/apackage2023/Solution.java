package lee.apackage2023;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/count-integers-with-even-digit-sum
    public int countEven(int num) {
        int ans = 0;
        for (int i = 1; i <= num; ++i) {
            int s = 0;
            for (int x = i; x > 0; x /= 10) {
                s += x % 10;
            }
            if (s % 2 == 0) {
                ++ans;
            }
        }
        return ans;
    }

    //    https://leetcode.cn/problems/check-if-numbers-are-ascending-in-a-sentence/
    public boolean areNumbersAscending(String s) {
        String[] arr = s.split("[^0-9]+");
        int max = Integer.MIN_VALUE;
        for (String item : arr) {
            if (item.equals("")) continue;
            int value = Integer.parseInt(item);
            if (max < value) max = value;
            else return false;
        }
        return true;
    }

    //    https://leetcode.cn/problems/min-max-game/
    public int minMaxGame(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        int[] newNums = new int[n / 2];
        for (int i = 0; i < newNums.length; i++) {
            if (i % 2 == 0) {
                newNums[i] = Math.min(nums[2 * i], nums[2 * i + 1]);
            } else {
                newNums[i] = Math.max(nums[2 * i], nums[2 * i + 1]);
            }
        }
        return minMaxGame(newNums);
    }

    //    https://leetcode.cn/problems/rearrange-characters-to-make-target-string/
    public int rearrangeCharacters(String s, String target) {
        Map<Character, Integer> sCounts = new HashMap<Character, Integer>();
        Map<Character, Integer> targetCounts = new HashMap<Character, Integer>();
        int n = s.length(), m = target.length();
        for (int i = 0; i < m; i++) {
            char c = target.charAt(i);
            targetCounts.put(c, targetCounts.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (targetCounts.containsKey(c)) {
                sCounts.put(c, sCounts.getOrDefault(c, 0) + 1);
            }
        }
        int ans = Integer.MAX_VALUE;

        for (Map.Entry<Character, Integer> entry : targetCounts.entrySet()) {
            char c = entry.getKey();
            int count = entry.getValue();
            int totalCount = sCounts.getOrDefault(c, 0);
            ans = Math.min(ans, totalCount / count);
            if (ans == 0) {
                return 0;
            }
        }


        return ans;
    }

    // https://leetcode.cn/problems/minimum-amount-of-time-to-fill-cups/
    public int UnTestFillCups(int[] amount) {
        Queue<Integer> qq = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        int res = 0;
        for (int num : amount) {
            qq.offer(num);
        }

        while (!qq.isEmpty()) {
            Integer v1 = qq.poll();
            Integer v2 = qq.poll();
            qq.offer(v1 - 1);
            if (v2 != null) {
                qq.offer(v2 - 1);
            }
            res++;
        }

        return res;
    }

    // https://leetcode.cn/problems/minimum-amount-of-time-to-fill-cups/solution/by-tsreaper-158c/
    public int fillCups(int[] amount) {
        Arrays.sort(amount);
        if (amount[0] + amount[1] <= amount[2]) return amount[2];
        else {
            int t = amount[0] + amount[1] - amount[2];
            return (t + 1) / 2 + amount[2];
        }
    }


    public static void main(String[] args) {
        var solu = new Solution();
        // 发送start split ignore

        /**
         * 中间有 “”
         * starting
         *  box has
         */
        solu.areNumbersAscending("starting 1 box has 3 blue 4 red 6 green and 12 yellow marbles5");
    }
}

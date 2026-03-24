package 面试题目.day1;

import 面试题目.DoubleCheck;
import 面试题目.NeedDeepLearn;

import java.util.*;

public class 子串 {

    // https://leetcode.cn/problems/subarray-sum-equals-k/submissions/608733528/?envType=study-plan-v2&envId=top-100-liked
    @DoubleCheck
    public int subarraySum(int[] nums, int k) {
        int count = 0;
        // 外层循环：以每个位置作为子数组的起始位置
        for (int start = 0; start < nums.length; start++) {
            int sum = 0; // 临时变量，用于计算从 start 位置开始的子数组和

            // 内层循环：从 start 位置向前遍历（向数组开头方向）
            // 这样可以计算所有以 start 为结尾的子数组的和
            for (int end = start; end >= 0; end--) {
                sum += nums[end]; // 累加当前元素到 sum
                if (sum == k) {
                    count++;
                }
            }
        }
        return count;
    }

    // https://leetcode.cn/problems/sliding-window-maximum/submissions/695212269/?envType=study-plan-v2&envId=top-100-liked
    @DoubleCheck
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        // 倒序
        // 创建最大堆（优先队列），存储[值, 索引]对
        // 自定义比较器：先按值降序，值相同则按索引降序
        PriorityQueue<int[]> pq = new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] pair1, int[] pair2) {
                return pair1[0] != pair2[0] ? pair2[0] - pair1[0] : pair2[1] - pair1[1];
            }
        });
        // 初始化：将第一个窗口的k个元素加入堆

        for (int i = 0; i < k; i++) {
            pq.offer(new int[]{nums[i], i});
        }
        // 结果数组，大小为 n-k+1（窗口个数）

        int[] ans = new int[n - k + 1];
        ans[0] = pq.peek()[0];

        for (int i = k; i < n; i++) {
            // 将新元素加入堆
            pq.offer(new int[]{nums[i], i});
            // 关键步骤：移除不在当前窗口内的堆顶元素
            // 当前窗口范围是 [i-k+1, i]，所以索引 <= i-k 的元素已过期
            while (pq.peek()[1] < i - k + 1) {
                pq.poll();
            }
            ans[i - k + 1] = pq.peek()[0];
        }
        return ans;
    }


    Map<Character, Integer> ori = new HashMap<>();

    Map<Character, Integer> cnt = new HashMap<>();


    @NeedDeepLearn
    public String minWindow(String s, String t) {
        int tLen = t.length();
        for (int i = 0; i < tLen; i++) {
            char c = t.charAt(i);
            ori.put(c, ori.getOrDefault(c, 0) + 1);
        }
        int l = 0, r = -1;
        int len = Integer.MAX_VALUE, ansL = -1, ansR = -1;
        int sLen = s.length();
        while (r < sLen) {
            ++r;
            if (r < sLen && ori.containsKey(s.charAt(r))) {
                cnt.put(s.charAt(r), cnt.getOrDefault(s.charAt(r), 0) + 1);
            }
            while (check() && l <= r) {
                if (r - l + 1 < len) {
                    // went wrong here
                    len = r - l + 1;
                    ansL = l;
                    ansR = l + len;
                }
                if (ori.containsKey(s.charAt(l))) {
                    // went wrong here
                    cnt.put(s.charAt(l), cnt.getOrDefault(s.charAt(l), 0) - 1);
                }
                ++l;
            }
        }
        return ansL == -1 ? "" : s.substring(ansL, ansR);
    }

    private boolean check() {
        for (Map.Entry<Character, Integer> entry : ori.entrySet()) {
            Character key = entry.getKey();
            Integer val = entry.getValue();

            if (cnt.getOrDefault(key, 0) < val) {
                return false;
            }
        }
        return true;
    }
}

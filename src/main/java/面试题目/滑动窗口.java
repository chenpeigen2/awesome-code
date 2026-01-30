package 面试题目;

import java.util.*;

public class 滑动窗口 {

    // https://leetcode.cn/problems/longest-substring-without-repeating-characters/submissions/615445771/?envType=study-plan-v2&envId=top-100-liked
    public int lengthOfLongestSubString(String s) {
        if (s.length() == 0) return 0;
        Map<Character, Integer> map = new HashMap<>();
        int max = 0;
        int left = 0;
        int right = 0;
        while (right < s.length()) {
            // 大部分时候其实这个并不会走到这里
            {
                if (map.containsKey(s.charAt(right))) {
                    // 这里其实就是在创造一个新的子串
                    left = Math.max(left, map.get(s.charAt(right)) + 1); // move to the new left
                }
                map.put(s.charAt(right), right);
            }
            max = Math.max(max, right - left + 1);
            right++;
        }
        return max;
    }

    // https://leetcode.cn/problems/find-all-anagrams-in-a-string/submissions/615450399/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        int[] cntP = new int[26];
        int[] cntS = new int[26];
        for (char c : p.toCharArray()) {
            cntP[c - 'a']++; // 统计p的字母
        }
        for (int right = 0; right < s.length(); right++) {
            cntS[s.charAt(right) - 'a']++; // 右端点字母进入窗口
            int left = right - p.length() + 1; // 固定滑动窗口
            if (left < 0) {
                continue;
            }
            if (Arrays.equals(cntP, cntS)) {
                ans.add(left);
            }
            cntS[s.charAt(left) - 'a']--; // 左端点字母离开窗口
        }
        return ans;
    }
}

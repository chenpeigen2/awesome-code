package 面试题目.day1;

import java.util.*;

// 滑动窗口的本质是 更新left 和 right
public class 滑动窗口 {

    // https://leetcode.cn/problems/longest-substring-without-repeating-characters/submissions/615445771/?envType=study-plan-v2&envId=top-100-liked
    /**
     * 计算字符串中最长无重复字符子串的长度
     * 使用滑动窗口算法，通过维护一个哈希表记录字符最后出现的位置
     * 
     * @param s 输入字符串
     * @return 最长无重复字符子串的长度
     */
    public int lengthOfLongestSubstring(String s) {
        // 初始化最大长度为0
        int max = 0;
        // 使用HashMap存储字符及其最后出现的位置
        Map<Character, Integer> map = new HashMap<>();
        // 滑动窗口左边界
        int left = 0;
        // 遍历字符串，right为滑动窗口右边界
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            // 如果当前字符已存在于map中，说明出现了重复
            if (map.containsKey(currentChar)) {
                // 更新左边界为max(当前左边界, 重复字符位置+1)
                // 确保窗口内没有重复字符
                left = Math.max(left, map.get(currentChar) + 1);
            }
            // 计算当前窗口长度并更新最大长度
            max = Math.max(max, right - left + 1);
            // 更新当前字符的最新位置
            map.put(currentChar, right);
        }
        return max;
    }

    // https://leetcode.cn/problems/find-all-anagrams-in-a-string/submissions/615450399/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        int[] cntP = new int[26];
        for (char ch : p.toCharArray()) {
            cntP[ch - 'a']++;
        }
        int[] cntS = new int[26];
        for (int right = 0;right < s.length();right++) {
            cntS[s.charAt(right) - 'a']++;
            int left = right - p.length() + 1;
            if (left < 0) {
                continue;
            }
            if (Arrays.equals(cntP, cntS)) {
                ans.add(left);
            }
            cntS[s.charAt(left) - 'a']--;
        }
        return ans;
    }
}

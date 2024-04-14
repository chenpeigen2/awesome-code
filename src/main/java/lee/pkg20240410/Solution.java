package lee.pkg20240410;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/maximum-binary-string-after-change/description/?envType=daily-question&envId=2024-04-10
    public String maximumBinaryString(String binary) {
        int idx = binary.indexOf('0');
        char[] ch = new char[binary.length()];
        Arrays.fill(ch, '1');
        int num_zero = 0;
        for (char character : binary.toCharArray()) {
            if (character == '0') {
                num_zero++;
            }
        }
        if (num_zero == 0) return binary;
        ch[num_zero + idx - 1] = '0';
        return new String(ch);
    }

    public String maximumBinaryString1(String binary) {
        int n = binary.length();
        char[] s = binary.toCharArray();
        int j = 0;
        for (int i = 0; i < n; i++) {
            // 0 1 0 ===> 101
            if (s[i] == '0') {
                while (j <= i || (j < n && s[j] == '1')) {
                    j++;
                }
                if (j < n) {
                    s[j] = '1';
                    s[i] = '1';
                    s[i + 1] = '0';
                }
            }
        }
        return new String(s);
    }

    // https://leetcode.cn/problems/integer-to-roman/description/?envType=study-plan-v2&envId=top-interview-150
    public String intToRoman(int num) {
        int[] numArr = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] str = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            for (int i = 0; i < numArr.length; i++) {
                if (numArr[i] <= num) {
                    sb.append(str[i]);
                    num -= numArr[i];
                    break;
                }
            }
        }
        return sb.toString();
    }

    //    https://leetcode.cn/problems/length-of-last-word/?envType=study-plan-v2&envId=top-interview-150
    public int lengthOfLastWord(String s) {
        int ans = 0;
        int len = s.length();
        boolean entered = false;
        for (int i = len - 1; i >= 0; i--) {
            if (!entered && s.charAt(i) == ' ') continue;
            if (s.charAt(i) != ' ') {
                entered = true;
                ans++;
            } else {
                return ans;
            }
        }
        return ans;
    }

    //    https://leetcode.cn/problems/longest-common-prefix/?envType=study-plan-v2&envId=top-interview-150
    public String longestCommonPrefix(String[] strs) {
        String s = strs[0];
        for (String str : strs) {
            while (true) {
                if (!str.startsWith(s)) {
                    s = s.substring(0, s.length() - 1);
                } else {
                    break;
                }
            }
        }
        return s;
    }

    //    https://leetcode.cn/problems/reverse-words-in-a-string/?envType=study-plan-v2&envId=top-interview-150
    public String reverseWords(String s) {
        s = s.strip();
        // 正则匹配连续的空白字符作为分隔符分割
        List<String> wordList = Arrays.asList(s.split("\\s+"));
        Collections.reverse(wordList);
        return String.join(" ", wordList);
    }

    //    https://leetcode.cn/problems/zigzag-conversion/description/?envType=study-plan-v2&envId=top-interview-150
    public String convert(String s, int numRows) {
        if (numRows < 2) return s;
        List<StringBuilder> rows = new ArrayList<StringBuilder>();
        for (int i = 0; i < numRows; i++) rows.add(new StringBuilder());
        int i = 0, flag = -1;
        for (char c : s.toCharArray()) {
            rows.get(i).append(c);
            if (i == 0 || i == numRows - 1) flag = -flag;
            i += flag;
        }
        StringBuilder res = new StringBuilder();
        for (StringBuilder row : rows) res.append(row);
        return res.toString();
    }

    //    https://leetcode.cn/problems/find-the-index-of-the-first-occurrence-in-a-string/?envType=study-plan-v2&envId=top-interview-150
    public int strStr(String haystack, String needle) {
        return haystack.indexOf(needle);
    }

    //    https://leetcode.cn/problems/valid-palindrome/?envType=study-plan-v2&envId=top-interview-150
    public boolean isPalindrome(String s) {
        int l = 0;
        int r = s.length() - 1;
        while (l < r) {
            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) {
                ++l;
            }
            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) {
                --r;
            }
            if (l < r) {
                if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) {
                    return false;
                }
                ++l;
                --r;
            }
        }
        return true;
    }

    //    https://leetcode.cn/problems/is-subsequence/?envType=study-plan-v2&envId=top-interview-150
    public boolean isSubsequence(String s, String t) {
        int idx = 0;
        int len = s.length();
        for (int i = 0; i < t.length(); i++) {
            if (idx >= len) break;
            if (s.charAt(idx) == t.charAt(i)) idx++;
        }
        return idx >= len;
    }

    //    https://leetcode.cn/problems/two-sum-ii-input-array-is-sorted/?envType=study-plan-v2&envId=top-interview-150
    public int[] twoSum(int[] numbers, int target) {
        int l = 0;
        int r = numbers.length - 1;
        while (l < r) {
            int sum = numbers[l] + numbers[r];
            if (sum == target) return new int[]{l + 1, r + 1};
            else if (sum > target) {
                r--;
            } else {
                l++;
            }
        }
        return new int[]{-1, -1};
    }

    //    https://leetcode.cn/problems/3sum/description/?envType=study-plan-v2&envId=top-interview-150
    public List<List<Integer>> threeSum(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        // 枚举 a
        for (int first = 0; first < n; ++first) {
            // 需要和上一次枚举的数不相同
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            // c 对应的指针初始指向数组的最右端
            int third = n - 1;
            int target = -nums[first];
            // 枚举 b
            for (int second = first + 1; second < third; ++second) {
                // 需要和上一次枚举的数不相同
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }
                // 需要保证 b 的指针在 c 的指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    --third;
                }
                // 如果指针重合，随着 b 后续的增加
                // 就不会有满足 a+b+c=0 并且 b<c 的 c 了，可以退出循环
                if (second == third) {
                    break;
                }
                if (nums[second] + nums[third] == target) {
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(nums[first]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    ans.add(list);
                }
            }
        }
        return ans;
    }

    //    https://leetcode.cn/problems/container-with-most-water/?envType=study-plan-v2&envId=top-interview-150
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int area = 0;
        while (left < right) {
            int tmp = (right - left) * Math.min(height[left], height[right]);
            area = Math.max(area, tmp);
            if (height[left] > height[right]) {
                right--;
            } else {
                left++;
            }
        }

        return area;
    }

    //    https://leetcode.cn/problems/minimum-size-subarray-sum/?envType=study-plan-v2&envId=top-interview-150
    public int minSubArrayLen(int target, int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;

        int ans = Integer.MAX_VALUE;
        int start = 0, end = 0;
        int sum = 0;
        while (end < n) {
            sum += nums[end++];
            while (sum >= target) {
                ans = Math.min(ans, end - start);
                sum -= nums[start++];
            }
        }
        return ans == Integer.MAX_VALUE ? 0 : ans;
    }

    //    https://leetcode.cn/problems/longest-substring-without-repeating-characters/?envType=study-plan-v2&envId=top-interview-150
    public int lengthOfLongestSubstring(String s) {
        if (s.length() == 0) return 0;
        Map<Character, Integer> map = new HashMap<>();
        int max = 0;
        int left = 0;
        int right = 0;
        while (right < s.length()) {
            if (map.containsKey(s.charAt(right))) {
                left = Math.max(left, map.get(s.charAt(right)) + 1); // move to new left
            }
            map.put(s.charAt(right), right);
            max = Math.max(max, right - left + 1);
            right++;
        }
        return max;
    }

}

package lee.pkg20240410;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

}

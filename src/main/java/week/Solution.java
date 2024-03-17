package week;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    //    https://leetcode.cn/problems/existence-of-a-substring-in-a-string-and-its-reverse/
    public boolean isSubstringPresent(String s) {
        Set<String> se = new HashSet<>();
        for (int i = 0; i < s.length() - 1; i++) {
            se.add(s.substring(i, i + 2));
        }
        String a = new StringBuilder(s).reverse().toString();
        for (int i = 0; i < s.length() - 1; i++) {
            if (se.contains(a.substring(i, i + 2))) return true;
        }
        return false;
    }

    //    https://leetcode.cn/problems/count-substrings-starting-and-ending-with-given-character/
    public long countSubstrings(String s, char c) {
        long k = s.chars().filter(ch -> ch == c).count();
        return (k + 1) * k / 2;
    }
}

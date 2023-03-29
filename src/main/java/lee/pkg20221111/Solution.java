package lee.pkg20221111;

public class Solution {
    //    https://leetcode.cn/problems/determine-if-string-halves-are-alike/
    public boolean halvesAreAlike(String s) {
        int len = s.length();
        if (len % 2 != 0) return false;
        int middle = len / 2;
        String s1 = s.substring(0, middle);
        String s2 = s.substring(middle);
        int a1 = 0;
        int a2 = 0;
        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);
            if (c1 == 'a' || c1 == 'e' || c1 == 'i' || c1 == 'o' || c1 == 'u') {
                a1++;
            }
            if (c2 == 'a' || c2 == 'e' || c2 == 'i' || c2 == 'o' || c2 == 'u') {
                a2++;
            }
        }
        return a1 == a2;
    }


    public boolean halvesAreAlike1(String s) {
        String a = s.substring(0, s.length() / 2);
        String b = s.substring(s.length() / 2);
        String h = "aeiouAEIOU";
        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < a.length(); i++) {
            if (h.indexOf(a.charAt(i)) >= 0) {
                sum1++;
            }
        }
        for (int i = 0; i < b.length(); i++) {
            if (h.indexOf(b.charAt(i)) >= 0) {
                sum2++;
            }
        }
        return sum1 == sum2;
    }
}

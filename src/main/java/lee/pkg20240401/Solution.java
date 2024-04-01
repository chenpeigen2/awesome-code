package lee.pkg20240401;

public class Solution {
    //    https://leetcode.cn/problems/faulty-keyboard/?envType=daily-question&envId=2024-04-01
    public String finalString(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            if (ch == 'i') sb.reverse();
            else sb.append(ch);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var str = "string";
        var solution = new Solution();
        var ans = solution.finalString(str);
        System.out.println(ans);
    }
}

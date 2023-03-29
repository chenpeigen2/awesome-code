package lee.pkg20220421;

public class Solution {
    //    https://leetcode-cn.com/problems/goat-latin/
    public String toGoatLatin(String s) {
        int n = s.length();
        String last = "a";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ) {
            int j = i;
            while (j < n && s.charAt(j) != ' ') j++;
            if ("aeiouAEIOU".indexOf(s.charAt(i)) >= 0) {
                sb.append(s, i, j).append("ma");
            } else {
                sb.append(s, i + 1, j).append(s.charAt(i)).append("ma");
            }

            sb.append(last);
            last += "a";

            i = j + 1; // update thing
            if (i < n) sb.append(" "); // append the blank space
        }
        return sb.toString();
    }
}

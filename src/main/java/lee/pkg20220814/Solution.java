package lee.pkg20220814;

public class Solution {

//    https://leetcode.cn/problems/maximum-score-after-splitting-a-string/
    public int maxScore(String s) {
        int len = s.length();
        int[] leftZero = new int[len];
        for (int i = 0, cnt = 0; i < len; i++) {
            if (s.charAt(i) == '0') cnt++;
            leftZero[i] = cnt;
        }

        int ans = -1;
        for (int i = 0; i < len - 1; i++) {
            ans = Math.max(ans, leftZero[i] + (len - (i + 1) - (leftZero[len - 1] - leftZero[i])));
        }

        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var cnt = app.maxScore("011101");
        System.out.println(cnt);
    }
}

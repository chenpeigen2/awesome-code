package lee.pkg20220329;

public class Solution {

    String s;
    int n, k;

    //    https://leetcode-cn.com/problems/maximize-the-confusion-of-an-exam/


    //    题目求修改次数不超过 kk 的前提下，连续段 'T' 或 'F' 的最大长度。

    //    等价于求一个包含 'F' 或者 'T' 的个数不超过 kk 的最大长度窗口。
    public int maxConsecutiveAnswers(String answerKey, int k) {
        s = answerKey;
        n = s.length();
        this.k = k;
        return Math.max(getCnt('T'), getCnt('F'));
    }

    public int maxConsecutiveAnswers1(String Key, int k) {
        char[] c = Key.toCharArray();
        int left = 0, right = 0, max = 0, T = 0, F = 0;
        while (right < c.length) {
            if (c[right++] == 'T') {
                T++;
            } else {
                F++;
            }
            while (T > k && F > k) {
                if (c[left++] == 'T') {
                    T--;
                } else {
                    F--;
                }
            }
            max = Math.max(max, (right - left));
        }
        return max;
    }

    int getCnt(char c) {
        int ans = 0;
        for (int i = 0, j = 0, cnt = 0; i < n; i++) {
            if (s.charAt(i) == c) cnt++;
            while (cnt > k) {
                if (s.charAt(j) == c) cnt--;
                j++;
            }
            // []
            ans = Math.max(ans, i - j + 1);
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.maxConsecutiveAnswers("TTTF", 2);
        System.out.println(ans);
    }
}

package lee.pkg20220424;

public class Solution {
    //    https://leetcode-cn.com/problems/binary-gap/
    public int binaryGap(int n) {
        String value = Integer.toBinaryString(n);
        int i = 0;
        for (; i < value.length(); i++) {
            if (value.charAt(i) == '1') break;
        }
        int j = i;
        int ans = 0;
        for (; j < value.length(); j++) {
            if (value.charAt(j) == '1') {
                ans = Math.max(ans, j - i);
                i = j;
            }
        }
        return ans;
    }

    //    https://blog.csdn.net/qq_22038259/article/details/113802302
    public int binaryGap1(int n) {
        int ans = 0;

        for (int i = 31, j = -1; i >= 0; i--) {
            if ((n >> i & 1) == 1) {
                if (j != -1) {
                    ans = Math.max(ans, j - i);
                }
                j = i;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.binaryGap(22);

        System.out.println(ans);
    }
}

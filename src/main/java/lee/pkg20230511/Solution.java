package lee.pkg20230511;

public class Solution {
    //    https://leetcode.cn/problems/binary-string-with-substrings-representing-1-to-n/
    public boolean queryString(String s, int n) {
        for (int i = 1; i <= n; i++) {
            if (!s.contains(Integer.toBinaryString(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(Integer.toBinaryString(8));
    }
}

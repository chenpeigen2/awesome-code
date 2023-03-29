package lee.pkg20220801;

public class Solution {
    //    https://leetcode.cn/problems/generate-a-string-with-characters-that-have-odd-counts/
    public String generateTheString(int n) {
        if (n % 2 == 1) {
            return "a".repeat(n);
        } else {
            return "a".repeat(n - 1) + "b";
        }
    }
}

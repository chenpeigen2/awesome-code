package lee.pkg20221103;

public class Solution {
    //    public int maxRepeating(String sequence, String word) {
//        int ans = 0;
//        int len = word.length();
//        for (int i = 0, j = 0; i < sequence.length(); i++) {
//            char c = sequence.charAt(i);
//            if (c != word.charAt(j)) {
//                j = 0;
//                continue;
//            }
//            j++;
//            if (j == len) {
//                ans++;
//                j = 0;
//            }
//        }
//        return ans;
//    }
//    https://leetcode.cn/problems/maximum-repeating-substring/description/
    public int maxRepeating(String sequence, String word) {
        String s = word;
        int ans = 0;
        while (sequence.contains(s)) {
            ans++;
            s += word;
        }
        return ans;
    }
}

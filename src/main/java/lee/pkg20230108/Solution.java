package lee.pkg20230108;

public class Solution {
    //    https://leetcode.cn/problems/counting-words-with-a-given-prefix/
    public int prefixCount(String[] words, String pref) {
        int cnt = 0;
        for (String word : words) {
            if (word.startsWith(pref)) cnt++;
        }
        return cnt;
    }
}

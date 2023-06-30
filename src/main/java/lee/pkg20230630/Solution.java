package lee.pkg20230630;

public class Solution {
    //    https://leetcode.cn/problems/circular-sentence/
    public boolean isCircularSentence(String sentence) {
        int len = sentence.length();
        if (sentence.charAt(0) != sentence.charAt(len-1)) return false;
        for (int i = 1;i < len-1;i++) {
            if (sentence.charAt(i) == ' ' && sentence.charAt(i-1) != sentence.charAt(i+1)) return false;
        }
        return true;
    }
}

package lee.pkg20220821;

public class Solution {
    //    https://leetcode.cn/problems/check-if-a-word-occurs-as-a-prefix-of-any-word-in-a-sentence/
    public int isPrefixOfWord(String sentence, String searchWord) {
        int idx = -1;
        String[] splits = sentence.split(" ");
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].startsWith(searchWord)) {
                idx = i;
                break;
            }
        }
        return idx;
    }
}

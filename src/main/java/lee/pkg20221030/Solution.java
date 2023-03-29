package lee.pkg20221030;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    //    https://leetcode.cn/problems/letter-case-permutation/
    public List<String> letterCasePermutation(String s) {
        List<String> ans = new ArrayList<>();
        char[] ch = s.toCharArray();
        dfs(ch, 0, ans);

        return ans;
    }

    void dfs(char[] ch, int index, List<String> ans) {
        if (index == ch.length) {
            ans.add(new String(ch));
            return;
        }
        dfs(ch, index + 1, ans);
        if (Character.isLetter(ch[index])) {
            ch[index] ^= 1 << 5;
            dfs(ch, index + 1, ans);
        }
    }

}

package lee.pkg20210907;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/maximum-value-of-a-string-in-an-array/solution/
    public int maximumValue(String[] strs) {
        int res = -1;
        for (String str : strs) {
            boolean isLetter = false;
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (Character.isLetter(ch)) {
                    isLetter = true;
                    break;
                }
            }
            res = Math.max(res, isLetter ? str.length() : Integer.parseInt(str));
        }
        return res;
    }
}

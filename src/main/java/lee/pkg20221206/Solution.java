package lee.pkg20221206;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    //    https://leetcode.cn/problems/number-of-different-integers-in-a-string/
    public int numDifferentIntegers(String word) {
        Set<String> set = new HashSet<>();
        for (String str : word.split("[a-z]+")) if (str.length() > 0) set.add(str.replaceAll("^0+",""));
        return set.size();
    }
}

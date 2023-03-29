package lee.pkg20211204;

import java.util.Arrays;

public class Solution {
    /**
     * @param ransomNote the random character we can predict
     * @param magazine   the character we can use
     * @return 为了不在赎金信中暴露字迹，从杂志上搜索各个需要的字母，组成单词来表达意思。
     * <p>
     * 给你一个赎金信 (ransomNote) 字符串和一个杂志(magazine)字符串，判断 ransomNote 能不能由 magazines 里面的字符构成。
     * <p>
     * 如果可以构成，返回 true ；否则返回 false 。
     * <p>
     * magazine 中的每个字符只能在 ransomNote 中使用一次。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/ransom-note
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    public boolean canConstruct(String ransomNote, String magazine) {
        int[] arr = new int[26];
        for (char character : magazine.toCharArray()) {
            arr[character - 'a']++;
        }
        for (char character : ransomNote.toCharArray()) {
            if ((--arr[character - 'a']) < 0) return false;
        }
        return true;
    }
}

package lee.pkg20231107;

public class Solution {
    //    https://leetcode.cn/problems/count-the-number-of-vowel-strings-in-range/?envType=daily-question&envId=2023-11-07
    public int vowelStrings(String[] words, int left, int right) {

        int ret = 0;
        while (left <= right) {
            String word = words[left];
            char front = word.charAt(0);
            char end = word.charAt(word.length() - 1);
            if ((front == 'a' || front == 'e' || front == 'i' || front == 'o' || front == 'u')

                    && ((end == 'a' || end == 'e' || end == 'i' || end == 'o' || end == 'u'))) {
                ret++;
            }
            left++;
        }

        return ret;

    }
}

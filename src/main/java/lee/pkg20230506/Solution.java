package lee.pkg20230506;

public class Solution {

    //    https://leetcode.cn/problems/minimum-number-of-frogs-croaking/solution/python3javacgotypescript-yi-ti-yi-jie-ji-lfn1/
    public int minNumberOfFrogs(String croakOfFrogs) {
        int n = croakOfFrogs.length();
        if (n % 5 != 0) {
            return -1;
        }
        int[] idx = new int[26];
        String s = "croak";
        for (int i = 0; i < 5; ++i) {
            idx[s.charAt(i) - 'a'] = i;
        }

        int[] cnt = new int[5];
        int ans = 0, x = 0;

        for (int k = 0; k < n; ++k) {
            int i = idx[croakOfFrogs.charAt(k) - 'a'];
            ++cnt[i];
            if (i == 0) {
                ans = Math.max(ans, ++x);
            } else {
                if (--cnt[i - 1] < 0) { // 没有东西可以给消减
                    return -1;
                }
                if (i == 4) { // 有青蛙完成了鸣叫
                    --x;
                }
            }
        }
        // cccceok
        // 完成了蛙鸣的时候x应该为0
        return x > 0 ? -1 : ans;
    }
}

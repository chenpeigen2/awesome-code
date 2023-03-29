package lee.pkg20220522;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    //    https://leetcode.cn/problems/can-i-win/
    Map<Integer, Boolean> memo = new HashMap<>(); // use memo to memory ji-yi-hua-search (opt)

    public boolean canIWin(int maxChoosableInteger, int desiredTotal) {
        if ((1 + maxChoosableInteger) * (maxChoosableInteger) / 2 < desiredTotal) return false;
//        return dfs(maxChoosableInteger, 0, desiredTotal, 0);
        return dfs1(maxChoosableInteger, 0, desiredTotal, 0);

    }

    private boolean dfs1(int maxChoosableInteger, int used, int desiredTotal, int sum) {
        if (!memo.containsKey(used)) {
            boolean flag = false;

            for (int i = 0; i < maxChoosableInteger; i++) {

                if (((used >> i) & 1) == 0) {
                    if (sum + 1 + i >= desiredTotal) {
                        flag = true;
                        break;
                    }
                    if (!dfs1(maxChoosableInteger, used | 1 << i, desiredTotal, sum + 1 + i)) {
                        flag = true;
                        break;
                    }
                }
            }

            memo.put(used, flag);
        }
        return memo.get(used);
    }


    private boolean dfs(int maxChoosableInteger, int usedNumbers, int desiredTotal, int currentTotal) {
        if (!memo.containsKey(usedNumbers)) {
            boolean res = false;
            for (int i = 0; i < maxChoosableInteger; i++) {
                // USE USEDnUMBERS AS CONFIG .
                // THIS MEANS IT DOESN'T USE
                if (((usedNumbers >> i) & 1) == 0) {
                    if ((i + 1) + currentTotal >= desiredTotal) {
                        res = true;
                        break;
                    }
                    if (!dfs(maxChoosableInteger, usedNumbers | (1 << i), desiredTotal, currentTotal + (i + 1))) {
                        res = true;
                        break;
                    }
                }
            }
            memo.put(usedNumbers, res);
        }
        return memo.get(usedNumbers);
    }
}

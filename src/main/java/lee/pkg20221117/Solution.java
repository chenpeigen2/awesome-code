package lee.pkg20221117;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/number-of-matching-subsequences/description/
    public int numMatchingSubseq(String s, String[] words) {
        List<Integer>[] pos = new List[26];
        for (int i = 0; i < 26; ++i) {
            pos[i] = new ArrayList<>();
        }
        // 记录出现的位置
        for (int i = 0; i < s.length(); ++i) {
            pos[s.charAt(i) - 'a'].add(i);
        }
        // nice try
        int res = words.length;
        for (String w : words) {
            if (w.length() > s.length()) {
                --res;
                continue;
            }

            int p = -1;
            for (int i = 0; i < w.length(); ++i) {

                char c = w.charAt(i);

                // pos[c - 'a'].get(pos[c - 'a'].size() - 1) 最后一个出现的idx位置
                if (pos[c - 'a'].isEmpty() || pos[c - 'a'].get(pos[c - 'a'].size() - 1) <= p) {
                    --res;
                    break;
                }

                p = binarySearch(pos[c - 'a'], p);
            }

        }
        return res;
    }

    /**
     * find the nearest target
     * 二分查找永远可以幻想成两个数 a b 进行比较
     * 重要的是处理关于mid = target的情况，这种情况十分重要
     */
    public int binarySearch(List<Integer> list, int target) {
        int left = 0, right = list.size() - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
//            if (list.get(mid) == target) return list.get(mid + 1);
            if (list.get(mid) > target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return list.get(left);
    }
}

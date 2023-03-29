package lee.pkg20221008;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/advantage-shuffle/
    public int[] advantageCount(int[] nums1, int[] nums2) {
        int n = nums1.length;
        Integer[] idx1 = new Integer[n];
        Integer[] idx2 = new Integer[n];
        for (int i = 0; i < n; i++) {
            idx1[i] = i;
            idx2[i] = i;
        }

        Arrays.sort(idx1, (i, j) -> nums1[i] - nums1[j]);
        Arrays.sort(idx2, (i, j) -> nums2[i] - nums2[j]);

        int[] ans = new int[n];
        int left = 0, right = n - 1;


        /**
         * 如果它能比过齐威王的下等马（\textit{nums}_2nums
         * 2
         * ​
         *   的最小值），那这一分田忌直接拿下；
         * 如果它比不过齐威王的下等马，则用田忌的下等马比齐威王的上等马（\textit{nums}_2nums
         * 2
         * ​
         *   的最大值）。
         *
         * 作者：endlesscheng
         * 链接：https://leetcode.cn/problems/advantage-shuffle/solution/tian-ji-sai-ma-by-endlesscheng-yxm6/
         * 来源：力扣（LeetCode）
         * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
         */

        for (int i = 0; i < n; i++) {
            // 如果nums1 最小的元素 大于nums2最小的元素
            if (nums1[idx1[i]] > nums2[idx2[left]]) {
                ans[idx2[left]] = nums1[idx1[i]];
                left++;
            } else {
                ans[idx2[right]] = nums1[idx1[i]];
                right--;
            }
        }

        return ans;
    }


    public int[] advantageCount1(int[] nums1, int[] nums2) {
        int n = nums1.length;
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            List<Integer> list = map.getOrDefault(nums2[i], new ArrayList<>());
            list.add(i);
            map.put(nums2[i], list); // 记录idx 位置
        }
        Arrays.sort(nums1); Arrays.sort(nums2);
        int[] ans = new int[n];
        for (int l1 = 0, l2 = 0, r2 = n - 1; l1 < n; l1++) {
            int t = nums1[l1] > nums2[l2] ? l2 : r2; // 如果赢了就行，输了就用最垃圾的马
            List<Integer> list = map.get(nums2[t]);
            int idx = list.remove(list.size() - 1); // remove the last
            ans[idx] = nums1[l1]; // 匹配马
            // left 和 right 更新
            if (t == l2) l2++;
            else r2--;
        }
        return ans;
    }
}

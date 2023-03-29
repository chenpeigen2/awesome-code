package lee.pkg20220723;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// TODO
public class Solution {
//    https://leetcode.cn/problems/ur2n8P/


//    public boolean sequenceReconstruction(int[] nums, int[][] sequences) {
//        int len = nums.length;
//        if (sequences.length != (len * (len - 1) / 2)) return false;
//        Arrays.sort(nums);
//        Arrays.sort(sequences, (a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);
//        for (int i = 0, cnt = 0; i < len - 1; i++) {
//            for (int j = i + 1; j < len; j++) {
//                int a = nums[i], b = nums[j];
//                if (a != sequences[cnt][0] || b != sequences[cnt][1]) return false;
//                cnt++;
//            }
//        }
//        return true;
//    }

}

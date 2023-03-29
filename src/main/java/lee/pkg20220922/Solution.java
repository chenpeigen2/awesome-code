package lee.pkg20220922;

public class Solution {

    //    https://leetcode.cn/problems/check-array-formation-through-concatenation/
    public boolean canFormArray(int[] arr, int[][] pieces) {
        int n = arr.length, m = pieces.length;
        int[] hash = new int[110];
        for (int i = 0; i < m; i++) hash[pieces[i][0]] = i;
        for (int i = 0; i < n; ) {
            int[] piece = pieces[hash[arr[i]]];
            int len = piece.length, idx = 0;
            while (idx < len && arr[i + idx] == piece[idx]) idx++;
            if (idx == len) i += len;
            else return false;
        }
        return true;
    }
}

package lee.pkg20230403;

public class Solution {
    //    https://leetcode.cn/problems/previous-permutation-with-one-swap/
    public int[] prevPermOpt1(int[] arr) {
        int len = arr.length;
        int curMax = -1;
        int index = -1;

        boolean hasResult = false;

        for (int i = len - 2; i >= 0; i--) {
            // 8 6 9 4 3 7
            if (arr[i] > arr[i + 1]) {

                for (int j = i + 1; j < len; j++) {
                    // [3,1,1,3]
                    if (arr[j] < arr[i]) {
                        hasResult = true;
                        if (arr[j] > curMax) {
                            curMax = arr[j];
                            index = j;
                        }
                    }
                }
            }
            if (hasResult) {
                int tmp = arr[i];
                arr[i] = arr[index];
                arr[index] = tmp;
                return arr;
            }
        }


        return arr;
    }
}
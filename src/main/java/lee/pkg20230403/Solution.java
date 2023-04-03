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

    public int[] prevPermOpt11(int[] arr) {
        int n = arr.length;
        // 5 1 2 3 4
        for (int i = n - 2; i >= 0; i--) {
            if (arr[i] > arr[i + 1]) {
                int j = n - 1;
                // 之前都是递增的
                // 找到递减的那个数字
                // 1 2 3 4 5
                // 1 2 2 2 3

//                [3,1,1,3]
//                [1,3,1,3]
                while (arr[j] >= arr[i] || arr[j] == arr[j - 1]) {
                    j--;
                }
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                break;
            }
        }
        return arr;
    }
}
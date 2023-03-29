package lee.pkg20220913;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/maximum-swap/
    public int maximumSwap(int num) {
        char[] charArray = String.valueOf(num).toCharArray();

        int n = charArray.length;
        int maxNum = num;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                swap(charArray, i, j);
                maxNum = Math.max(maxNum, Integer.parseInt(new String(charArray)));
                swap(charArray, i, j);
            }
        }

        return maxNum;

    }


    public int maximumSwap1(int num) {

        List<Integer> list = new ArrayList<>();
        while (num != 0) {
            list.add(num % 10);
            num /= 10;
        }

        int n = list.size(), ans = 0;
        int[] idx = new int[n];
        for (int i = 0, j = 0; i < n; i++) {
            if (list.get(i) > list.get(j)) j = i;
            idx[i] = j;
        }

        for (int i = n - 1; i >= 0; i--) {
            if (list.get(idx[i]) != list.get(i)) {
                int c = list.get(idx[i]);
                list.set(idx[i], list.get(i));
                list.set(i, c);
                break;
            }
        }
        for (int i = n - 1; i >= 0; i--) {
            ans = ans * 10 + list.get(i);
        }

        return ans;
    }

    public void swap(char[] charArray, int i, int j) {
        char temp = charArray[i];
        charArray[i] = charArray[j];
        charArray[j] = temp;
    }

    public static void main(String[] args) {
        var str = 9973;
        var app = new Solution();
        app.maximumSwap1(str);
    }
}

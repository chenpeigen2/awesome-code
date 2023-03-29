package lee.pkg20211115;

import java.util.Arrays;
import java.util.Random;

public class Solution {
    public int bulbSwitch(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        int[] arr = new int[n + 1];

        for (int i = 2; i <= n; i++) {
            if (i == 2) {
                for (int j = 1; j <= n; j += 2) {
                    arr[j] = 1;
                }
            } else {
                for (int j = i; j <= n; j += i) {
                    if (arr[j] == 0) {
                        arr[j] = 1;
                    } else {
                        arr[j] = 0;
                    }
                }

            }

        }
        return Arrays.stream(arr).sum();
    }


    public int bulbSwitch1(int n) {
        return (int) Math.sqrt(n);
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.bulbSwitch(5);
        System.out.println(result);
    }
}

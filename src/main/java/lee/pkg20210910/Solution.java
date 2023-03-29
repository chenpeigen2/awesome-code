package lee.pkg20210910;

public class Solution {
    public int chalkReplacer(int[] chalk, int k) {
        int n = chalk.length;
        long sum = 0;
        for (int j : chalk) {
            sum += j;
        }
        long remain = k % sum;

        int i = 0;
        for (; i < n; i++) {
            if (remain < chalk[i]) {
                break;
            }
            remain -= chalk[i];
        }
        return i;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{5, 1, 5};
        var result = new Solution().chalkReplacer(arr, 22);
        System.out.println(result);
    }
}

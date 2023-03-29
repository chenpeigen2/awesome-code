package lee.pkg20211203;

import java.util.Arrays;

public class Solution {

    public int largestSumAfterKNegations(int[] nums, int k) {
        Arrays.sort(nums);
        for (int i = 0; i < k; i++) {
            nums[0] = -nums[0];
            Arrays.sort(nums);
        }

        return Arrays.stream(nums).sum();
    }

    public int largestSumAfterKNegations1(int[] A, int K) {
        int[] number = new int[201];//-100 <= A[i] <= 100,这个范围的大小是201
        for (int t : A) {
            number[t + 100]++;//将[-100,100]映射到[0,200]上
        }
        int i = 0;
        while (K > 0) {
            while (number[i] == 0)//找到A[]中最小的数字
                i++;
            number[i]--;//此数字个数-1
            number[200 - i]++;//其相反数个数+1
            if (i > 100) {//若原最小数索引>100,则新的最小数索引应为200-i.(索引即number[]数组的下标)
                i = 200 - i;
            }
            K--;
        }
        int sum = 0;
        for (int j = i; j < number.length; j++) {//遍历number[]求和
            sum += (j - 100) * number[j];//j-100是数字大小,number[j]是该数字出现次数.
        }
        return sum;
    }
}

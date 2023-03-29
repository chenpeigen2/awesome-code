package lee.pkg20210911;

public class Solution {

    public int findIntegers(int n) {
        int N = 40;
        int[][] f = new int[N][2];

        f[1][0] = 1;
        f[1][1] = 2;

        for (int i = 2; i < N; i++) {
            // j = 0 的条件
            //如果j是0，满足条件的就有00，01，就是上面长度为1，j为1时的数量，
            f[i][0] = f[i - 1][1];
            //如果j是1，j-1 = 1 条件满足
            // 还有就是最高位为1，他的前一位必须为0，那么就是长度为1，j为0的情况 == 2
            f[i][1] = f[i - 1][0] + f[i][0];
        }

        int len = 0;
        while (n >> len != 0) {
            len++;
        }

        int ans = 0;
        int cur = 0, pre = 0;


        for (int i = len; i >= 0; i--) {
            int offset = i - 1;
            cur = (n >> offset) & 1;

            // 1 包括0 ，单身0不包括1
            if (cur == 1) {
                ans += f[i][0];
            }
            if (cur == 1 && pre == 1) break;

            // 补齐最后一个1模式下缺少的1
            // 1 0 0 0 0
            //[1] -> [0] 少了一位数
            // 把自己算进去
            // 需要配合决策树观看才好理解
            if (offset == 0){
                ans++;
            }
            pre = cur;
        }
        return ans;
    }

    public static void main(String[] args) {
        var a = new Solution().findIntegers(5);
        System.out.println(a);
    }
}
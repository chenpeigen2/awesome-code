package lee.pkg20220115;

public class Solution {
    //    https://leetcode-cn.com/problems/calculate-money-in-leetcode-bank/solution/
    public int totalMoney(int n) {
        int left = n % 7;
        int cnt = n / 7;
        int ans = 0;
        for (int i = 0, flag = 28; i < cnt; i++, flag += 7) {
            ans += flag;
        }
        ans += ((1 + cnt * 2 + left) * left) / 2;
        return ans;
    }

    public int totalMoney1(int n) {
        // 所有完整的周存的钱
        int weekNumber = n / 7;
        int firstWeekMoney = (1 + 7) * 7 / 2;
        int lastWeekMoney = firstWeekMoney + 7 * (weekNumber - 1);
        int weekMoney = (firstWeekMoney + lastWeekMoney) * weekNumber / 2;
        // 剩下的不能构成一个完整的周的天数里存的钱
        int dayNumber = n % 7;
        int firstDayMoney = 1 + weekNumber;
        int lastDayMoney = firstDayMoney + dayNumber - 1;
        int dayMoney = (firstDayMoney + lastDayMoney) * dayNumber / 2;
        return weekMoney + dayMoney;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.totalMoney(20);
        System.out.println(ans);
    }
}

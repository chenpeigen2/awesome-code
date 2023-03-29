package lee.pkg20221104;

public class Solution {
    //    https://leetcode.cn/problems/reach-a-number/
    public int reachNumber(int target) {
        int result = 0, num = 0, t = Math.abs(target); // 由于target有负数情况，为了统一计算逻辑，所以取绝对值
        // 直到num值大于等于t，并且num减t是偶数，才结束while循环
        while (num < t || (num - t) % 2 != 0) num += ++result;
        return result;
//        作者：爪哇缪斯
//        链接：https://leetcode.cn/problems/reach-a-number/solutions/1947300/by-muse-77-g0il/
//        来源：力扣（LeetCode）
//        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
    }
}

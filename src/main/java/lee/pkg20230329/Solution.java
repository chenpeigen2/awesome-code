package lee.pkg20230329;

public class Solution {

    //    https://leetcode.cn/problems/count-sorted-vowel-strings/solution/zhong-xue-shu-xue-ke-pu-n-ge-xiao-qiu-fang-dao-m-g/

    /**
     * 将 n 个小球放到 m 个盒子里，盒子不为空：C(n - 1, m - 1)；
     * <p>
     * 将 n 个小球放到 m 个盒子里，盒子可以空：C(n + m - 1, m - 1)；
     * <p>
     * 所以答案是 C(n + 5 - 1, 5 - 1) = C(n + 4, 4)
     */

    public int countVowelStrings(int n) {
        return (n + 4) * (n + 3) * (n + 2) * (n + 1) / 24;
    }

    public static void main(String[] args) {
        System.out.println("555");
    }
}

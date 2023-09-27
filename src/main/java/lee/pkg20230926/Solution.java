package lee.pkg20230926;

public class Solution {
    //    https://leetcode.cn/problems/pass-the-pillow/description/?envType=daily-question&envId=2023-09-26
    public int passThePillow(int n, int time) {
        int left = time % (n - 1);
        int count = time / (n - 1);
        if (count % 2 == 0) {
            return left + 1;
        } else {
            return n - left;
        }
    }
}

package lee.pkg20230926;

public class Solution {
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

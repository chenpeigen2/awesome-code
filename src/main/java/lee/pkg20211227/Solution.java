package lee.pkg20211227;

import java.util.Arrays;

public class Solution {
    public int numFriendRequests(int[] ages) {
        Arrays.sort(ages);
        int n = ages.length;
        int left = 0, right = 0, ans = 0;
        for (int age : ages) {
            // age[x] <= 14 不满足
            if (age < 15) continue;
            // ages[left] > 0.5 * age +7
            while (ages[left] <= 0.5 * age + 7) ++left;
            // ages[right] <= age
            while (right + 1 < n && ages[right + 1] <= age) ++right;
            // and x was in [left , right] , so
            ans += (right - left);
        }
        return ans;
    }
}

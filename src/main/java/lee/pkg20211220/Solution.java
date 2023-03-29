package lee.pkg20211220;

import java.util.Arrays;

public class Solution {
    public int findRadius(int[] houses, int[] heaters) {
        Arrays.sort(houses);
        Arrays.sort(heaters);
        int l = 0, r = (int) 1e9;
        while (l < r) {
            int mid = l + r >> 1;
            if (check(houses, heaters, mid)) r = mid;
            else l = mid + 1;
        }
        return r;
    }

    boolean check(int[] houses, int[] heaters, int x) {
        int n = houses.length, m = heaters.length;
        for (int i = 0, j = 0; i < n; i++) {
            while (j < m && houses[i] > heaters[j] + x) j++;
            if (j < m && heaters[j] - x <= houses[i] && houses[i] <= heaters[j] + x) continue;
            return false;
        }
        return true;
    }


    /**
     * better than prev
     *
     * @param houses
     * @param heaters
     * @return
     */
    public int findRadius1(int[] houses, int[] heaters) {
        Arrays.sort(houses);
        Arrays.sort(heaters);
        int i = 0, j = 0;
        int ans = 0;
        while (i < houses.length) {
            while (j < heaters.length && houses[i] >= heaters[j]) {
                j++;
            }
            // heaters[j] > houses[i] or j == heaters.length
            if (j == 0) {
                ans = Math.max(ans, heaters[j] - houses[i]);
            } else if (j == heaters.length) {
                // just why ok ，we just get it
                ans = Math.max(ans, houses[i] - heaters[j - 1]);
            } else {
                ans = Math.max(ans, Math.min(heaters[j] - houses[i], houses[i] - heaters[j - 1]));
            }
            i++;
        }
        return ans;
    }

    public int findRadius2(int[] houses, int[] heaters) {
        Arrays.sort(houses);
        Arrays.sort(heaters);
        int ans = 0;
        for (int i = 0, j = 0; i < houses.length; i++) {
            // find a stop
            for (; j < heaters.length && heaters[j] <= houses[i]; j++) ;
            // if they over the upper looper,
            // it can be specific j == 0
            // j == length
            // j in the middle of sth
            if (j == 0) {
                ans = Math.max(ans, heaters[j] - houses[i]);
            } else if (j == heaters.length) {
                ans = Math.max(ans, houses[i] - heaters[j - 1]);
            } else {
                ans = Math.max(ans, Math.min(heaters[j] - houses[i], houses[i] - heaters[j - 1]));
            }
        }
        return ans;
    }
}

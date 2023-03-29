package lee.pkg20220118;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Solution {


    public int findMinDifference(List<String> timePoints) {
        Collections.sort(timePoints);
        int ans = Integer.MAX_VALUE;
        for (int i = 1; i < timePoints.size(); i++) {
            ans = Math.min(ans, getMinutes(timePoints.get(i)) - getMinutes(timePoints.get(i - 1)));
        }
        ans = Math.min(ans, getMinutes(timePoints.get(0)) + 1440 - getMinutes(timePoints.get(timePoints.size() - 1)));
        return ans;
    }


    public int getMinutes(String t) {
        return ((t.charAt(0) - '0') * 10 + (t.charAt(1) - '0')) * 60 + (t.charAt(3) - '0') * 10 + (t.charAt(4) - '0');
    }
}

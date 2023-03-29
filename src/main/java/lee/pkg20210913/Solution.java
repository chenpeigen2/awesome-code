package lee.pkg20210913;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public int numberOfBoomerangs(int[][] points) {
        int result = 0;
        int pointsCount = points.length;
        for (int i = 0; i < pointsCount; i++) {
            Map<Integer, Integer> m = new HashMap<>();
            for (int j = 0; j < pointsCount; j++) {
                if (i == j) continue;
                int offsetX = points[i][0] - points[j][0];
                int offsetY = points[i][1] - points[j][1];
                int dst = offsetX * offsetX + offsetY * offsetY;
                m.put(dst, m.getOrDefault(dst, 0) + 1);
            }

            for (int key : m.keySet()) {
                int val = m.get(key);
                result = result + val * (val - 1);
            }
        }
        return result;
    }
}

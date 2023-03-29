package lee.pkg20220413;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//https://leetcode-cn.com/problems/insert-delete-getrandom-o1/
public class RandomizedSet {

    static int[] nums = new int[200010];

    Random r = new Random();
    Map<Integer, Integer> m = new HashMap<>();
    int idx = -1;

    public RandomizedSet() {
    }

    /**
     * @param val the key we should input
     * @return true if succeed
     */
    public boolean insert(int val) {
        if (m.containsKey(val)) return false;
        nums[++idx] = val;
        m.put(val, idx);
        return true;
    }

    public boolean remove(int val) {
        if (!m.containsKey(val)) return false;
        int loc = m.remove(val); // the idx the val hold
        if (loc != idx) m.put(nums[idx], loc); // not the last, then put the map last insert-value into the loc
        nums[loc] = nums[idx--];
        return true;
    }

    public int getRandom() {
        return nums[r.nextInt(idx + 1)];
    }
}

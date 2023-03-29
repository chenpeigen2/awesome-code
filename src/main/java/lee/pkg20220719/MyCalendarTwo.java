package lee.pkg20220719;

import java.util.ArrayList;
import java.util.List;

//https://leetcode.cn/problems/my-calendar-ii/
public class MyCalendarTwo {

    List<int[]> booked;

    List<int[]> overlaps;

    public MyCalendarTwo() {
        booked = new ArrayList<>();
        overlaps = new ArrayList<>();
    }

    public boolean book(int start, int end) {
        for (int[] arr : overlaps) {
            int l = arr[0], r = arr[1];
            if (l < end && r > start) return false;
        }
        for (int[] arr : booked) {
            int l = arr[0], r = arr[1];
            // add the overlap area
            if (end > l && start < r) {
                overlaps.add(new int[]{Math.max(l, start), Math.min(r, end)});
            }
        }
        booked.add(new int[]{start, end});
        return true;
    }
}

package lee.pkg20220404;

import java.util.Arrays;

//https://leetcode-cn.com/problems/range-sum-query-mutable/

//https://www.cnblogs.com/xenny/p/9739600.html
public class NumArray {

    // start from 1
    int[] tree;

    int lowbit(int x) {
        return x & -x;
    }

    int query(int i) {  //求A[1 - i]的和
        int res = 0;
        while (i > 0) {
            res += tree[i];
            i -= lowbit(i);
        }
        return res;
    }

    void add(int i, int k) { //在i位置加上k
        while (i <= n) {
            tree[i] += k;
            i += lowbit(i);
        }
    }


    private int[] nums;

    int n;


    public NumArray(int[] nums) {
        this.nums = nums;
        n = nums.length;
        tree = new int[n + 1];
        for (int i = 0; i < n; i++) {
            add(i + 1, nums[i]);
        }
    }

    public void update(int index, int val) {
        // 10 -> 4 so we need add -6 in the spec location
        add(index + 1, val - nums[index]);
        nums[index] = val;
    }

    public int sumRange(int left, int right) {
        return query(right + 1) - query(left);
    }

    public static void main(String[] args) {
        var app = new NumArray(new int[]{1, 3, 5});
        System.out.println(app.sumRange(0, 2));
    }
}

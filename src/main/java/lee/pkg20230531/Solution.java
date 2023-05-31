package lee.pkg20230531;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/minimum-cost-tree-from-leaf-values/
    //    https://leetcode.cn/problems/minimum-cost-tree-from-leaf-values/solution/wei-shi-yao-dan-diao-di-jian-zhan-de-suan-fa-ke-xi/
    // 通过维护一个单调递减栈就可以找到一个极小值
    public int mctFromLeafValues(int[] arr) {

        Deque<Integer> st = new ArrayDeque<>();
        st.push(Integer.MAX_VALUE);
        int mct = 0;

        for (int i = 0; i < arr.length; i++) {
            while (arr[i] >= st.peek()) {
                mct += st.pop() * Math.min(st.peek(), arr[i]);
            }
            st.push(arr[i]);
        }
        //
        while (st.size() > 2) {
            mct += st.pop() * st.peek();
        }
        return mct;
    }
}

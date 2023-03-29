package lee.pkg20221020;

public class Solution {


    //    https://leetcode.cn/problems/k-th-symbol-in-grammar/
        public int kthGrammar(int n, int k) {
            if (n == 1) {
                return 0;
            }
            return (k & 1) ^ 1 ^ kthGrammar(n - 1, (k + 1) / 2);
        }
}

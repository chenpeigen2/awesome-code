package lee.pkg20231106;

public class Solution {
    //    https://leetcode.cn/problems/maximum-product-of-word-lengths/description/?envType=daily-question&envId=2023-11-06
    public int maxProduct(String[] words) {
        int[] arr = new int[words.length];
        int idx = 0;
        for (String s : words) {
            int result = 0;
            for (int i = 0; i < s.length(); i++) {
                int offset = s.charAt(i) - 'a';
                result |= (1 << offset);
            }
            arr[idx++] = result;
        }
        int ans = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < i; j++) {
                if ((arr[i] & arr[j]) == 0) {
                    int tmp = words[i].length() * words[j].length();
                    ans = Math.max(ans, tmp);
                }
            }
        }
        return ans;
    }
}

package lee.pkg20240410;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/maximum-binary-string-after-change/description/?envType=daily-question&envId=2024-04-10
    public String maximumBinaryString(String binary) {
        int idx = binary.indexOf('0');
        char[] ch = new char[binary.length()];
        Arrays.fill(ch, '1');
        int num_zero = 0;
        for (char character : binary.toCharArray()) {
            if (character == '0') {
                num_zero++;
            }
        }
        if (num_zero == 0) return binary;
        ch[num_zero + idx - 1] = '0';
        return new String(ch);
    }

    public String maximumBinaryString1(String binary) {
        int n = binary.length();
        char[] s = binary.toCharArray();
        int j = 0;
        for (int i = 0; i < n; i++) {
            // 0 1 0 ===> 101
            if (s[i] == '0') {
                while (j <= i || (j < n && s[j] == '1')) {
                    j++;
                }
                if (j < n) {
                    s[j] = '1';
                    s[i] = '1';
                    s[i + 1] = '0';
                }
            }
        }
        return new String(s);
    }
}

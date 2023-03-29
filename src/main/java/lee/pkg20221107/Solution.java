package lee.pkg20221107;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/ambiguous-coordinates/description/
    public List<String> ambiguousCoordinates(String s) {
        int n = s.length() - 2;
        List<String> res = new ArrayList<>();

        s = s.substring(1, s.length() - 1);

        for (int l = 1; l < n; ++l) {
            List<String> lt = getPos(s.substring(0, l));
            if (lt.isEmpty()) {
                continue;
            }
            List<String> rt = getPos(s.substring(l));
            if (rt.isEmpty()) {
                continue;
            }
            for (String i : lt) {
                for (String j : rt) {
                    res.add("(" + i + ", " + j + ")");
                }
            }
        }

        return res;
    }

    /**
     * 不添加小数点和添小数点后的整数部分都需要满足当前表示数字为 000 或者为不含前导零的正数。
     * 添加小数点后，小数部分需要满足其末尾不为 000。
     */
    public List<String> getPos(String s) {
        List<String> pos = new ArrayList<>();
        if (s.charAt(0) != '0' || "0".equals(s)) {
            pos.add(s);
        }
        for (int p = 1; p < s.length(); ++p) {
            if ((p != 1 && s.charAt(0) == '0') || s.charAt(s.length() - 1) == '0') {
                continue;
            }
            pos.add(s.substring(0, p) + "." + s.substring(p));
        }
        return pos;
    }
}

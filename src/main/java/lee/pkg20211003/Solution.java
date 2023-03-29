package lee.pkg20211003;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public String fractionToDecimal(int numerator, int denominator) {
        long a = numerator, b = denominator;
        if (a % b == 0) {
            return String.valueOf(a / b);
        }
        StringBuilder sb = new StringBuilder();
        if (a * b < 0) {
            sb.append("-");
        }
        a = Math.abs(a);
        b = Math.abs(b);

        // 计算小数点前的部分，并将余数赋值给 a
        sb.append(String.valueOf(a / b) + ".");

        a %= b;

        Map<Long, Integer> m = new HashMap<>();

//        首先可以明确，两个数相除要么是「有限位小数」，要么是「无限循环小数」，而不可能是「无限不循环小数」。

//        这引导我们可以在模拟竖式计算（除法）过程中，使用「哈希表」记录某个余数最早在什么位置出现过，
//        一旦出现相同余数，则将「出现位置」到「当前结尾」之间的字符串抠出来，即是「循环小数」部分。

        while (a != 0) {
            m.put(a, sb.length());
            a *= 10;
            sb.append(a / b);
            // the a has changed
            a %= b;

            if (m.containsKey(a)) {
                int u = m.get(a);
//                [)
                return String.format("%s(%s)", sb.substring(0, u), sb.substring(u));
            }
        }

        return sb.toString();
    }
}

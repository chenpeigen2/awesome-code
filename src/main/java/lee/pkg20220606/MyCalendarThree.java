package lee.pkg20220606;



import org.objectweb.asm.Type;

import java.util.Map;
import java.util.TreeMap;

/**
 * 方法一：差分数组
 * 思路与算法
 * <p>
 * 可以参考「731. 我的日程安排表 II」的解法二，我们可以采用同样的思路即可。利用差分数组的思想，每当我们预定一个新的日程安排 [\textit{start}, \textit{end})[start,end)，在 \textit{start}start 计数 \textit{cnt}[\textit{start}]cnt[start] 加 11，表示从 \textit{start}start 预定的数目加 11；从 \textit{end}end 计数 \textit{cnt}[\textit{end}]cnt[end] 减 11，表示从 \textit{end}end 开始预定的数目减 11。此时以起点 xx 开始的预定的数目 \textit{book}_x = \sum_{y \le x}\textit{cnt}[y]book
 * x
 * ​
 * =∑
 * y≤x
 * ​
 * cnt[y]，我们对计数进行累加依次求出最大的预定数目即可。由于本题中 \textit{start}, \textit{end}start,end 数量较大，我们利用 \texttt{TreeMap}TreeMap 计数即可。
 * <p>
 * 作者：LeetCode-Solution
 * 链接：https://leetcode.cn/problems/my-calendar-iii/solution/wo-de-ri-cheng-an-pai-biao-iii-by-leetco-9rif/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */

@lee.pkg20220606.xxx.Hello(fuzz = "xdxxx")
public class MyCalendarThree {
    //    https://leetcode.cn/problems/my-calendar-iii/
    private TreeMap<Integer, Integer> cnt;


    public MyCalendarThree() {
        cnt = new TreeMap<>();
    }

    public int book(int start, int end) {
        int ans = 0;
        int maxBook = 0;

        cnt.put(start, cnt.getOrDefault(start, 0) + 1);
        cnt.put(end, cnt.getOrDefault(end, 0) - 1);
        for (Map.Entry<Integer, Integer> entry : cnt.entrySet()) {
            maxBook += entry.getValue();
            ans = Math.max(ans, maxBook);
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(1);

       var t =  Type.getType(lee.pkg20220606.xxx.Hello.class);
        System.out.println(t.getClassName());
    }
}



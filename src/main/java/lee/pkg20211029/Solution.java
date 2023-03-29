package lee.pkg20211029;

public class Solution {
    public boolean isSelfCrossing(int[] d) {
        int n = d.length;
        if (n < 4) return false;
        for (int i = 3; i < n; i++) {
            if (d[i] >= d[i - 2] && d[i - 1] <= d[i - 3]) return true;
            if (i >= 4 && d[i - 1] == d[i - 3] && d[i] + d[i - 4] >= d[i - 2]) return true;
            if (i >= 5 && d[i - 1] <= d[i - 3] && d[i - 2] > d[i - 4] && d[i] + d[i - 4] >= d[i - 2] && d[i - 1] + d[i - 5] >= d[i - 3])
                return true;
        }
        return false;
    }


//    一直向外卷，如下图所示，这样是不会相交的，这种是比较好检查的，只要对于每一个 i来说 dist[i] > dist[i-2] 就一直不会相遇。

//    一直向内卷，如下图所示，这样也不会相交，这种也比较好检查，只要对于每一个 i来说 dist[i] < dist[i-2] 就一直不会相遇。

//    外卷转内卷，如下图所示，这种分情况讨论，只有出现下面这种情况才不会相交，而我们只要在外卷内卷交界处做一个特殊处理就可以转换成上面两种情况，
//    即 dist[i] > dist[i-2] - dist[i-4] 时，从 i-1 处剪成两半。

    public boolean isSelfCrossing1(int[] d) {
        int n = d.length;
        if (n < 4) return false;
        int i = 2;
        // 一直向外卷
        while (i < n && d[i] > d[i - 2]) i++;
        // 如果走完了，直接就可以返回不相交
        if (i == n) return false;
        // 如果向外卷转成了向内卷，i-1的长度减i-3的长度
        if (d[i] >= d[i - 2] - (i < 4 ? 0 : d[i - 4])) {
            d[i - 1] -= i < 3 ? 0 : d[i - 3];
        }
        //    [3,3,3,2,1,1]  case : false ++i
        // i+1 和 i-1 比较
        // 一直向内卷，注意i先加1，这样正好跟i-1的位置相比较，相当于内卷从i-1的位置开始的
        for (++i; i < n && d[i] < d[i - 2]; i++) ;
        // 如果 i 能走完就不会相交，相反，走不完就相交了
        return i != n;
    }


}

package lee.pkg20211006;

public class Solution {

//    public int thirdMax(int[] nums) {
//        Set<Integer> set = new HashSet<>();
//        for (int tmp : nums) {
//            set.add(tmp);
//        }
//        List<Integer> l = set.stream().toList();
//
//        l.sort((o1, o2) -> {
//            if (o1 < o2) {
//                return 1;
//            } else if (o1 > o2) {
//                return -1;
//            }
//            return 0;
//        });
//
//        return l.size() < 3 ? l.get(0) : l.get(2);
//    }


    // 10 e 18 次方
    long INF = (long) -1e18;

    public int thirdMax(int[] nums) {
        // a > b > c x
        long a = INF, b = INF, c = INF;

        for (int x :
                nums) {
            if (x > a) {
                c = b;
                b = a;
                a = x;
            } else if (x > b && x < a) {
                c = b;
                b = x;
            } else if (x > c && x < b) {
                c = x;
            }
        }
        return (int) (c != INF ? c : a);
    }
}

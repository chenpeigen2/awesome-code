package lee.pkg;

//git remote add origin https://gitee.com/HybridGaw/demo_java.git
// git push -u origin master

//mkdir demo_java
//        cd demo_java
//        git init
//        touch README.md
//        git add README.md
//        git commit -m "first commit"
//        git remote add origin https://gitee.com/HybridGaw/demo_java.git
//        git push -u origin master

//git config --global user.name "HybridGaw"
//        git config --global user.email "chan_1831938181@outlook.com"

import java.util.PriorityQueue;

public class App {
    public static void main(String[] args) {
        PriorityQueue<Integer> p = new PriorityQueue<Integer>((a, b) -> (b - a));
        p.add(3);
        p.add(1);
        p.add(2);
        p.add(4);
//       int[] arr =  p.toArray();

        p.stream().forEach(x -> {
            System.out.println(x);
        });

        System.out.println(p.poll());


//        while (!p.isEmpty()) {
//            System.out.println(p.remove());
//        }
    }
}
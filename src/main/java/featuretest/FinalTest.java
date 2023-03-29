package featuretest;

class Node {
    public int val = 1;
    public String value = "helloworld";

    public void setVal(int val) {
        this.val = val;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "val=" + val +
                ", value='" + value + '\'' +
                '}';
    }
}

// final 只会保证不会改变指向的位置，可以改变指向的内容
public class FinalTest {
    public static void main(String[] args) {
        Node n = new Node();
        final Node nl = n;

        final Node n2 = new Node();
        System.out.println(n2);
        n2.setValue("fucking amazing");
        System.out.println(n2);

        System.out.println(nl);

        n.setVal(3);
        n.setValue("fdsfsdf");

        System.out.println(nl);
    }
}

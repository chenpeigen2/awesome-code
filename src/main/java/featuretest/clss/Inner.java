package featuretest.clss;

public interface Inner {
    void ccc();

    static Inner getInstance(String c2z) {

        String cc2 = "xxYzbb";

        return new Inner() {
            @Override
            public void ccc() {
                String c1 = c2z.toLowerCase();

                System.out.println(c1);
            }

            public void ddd() {
                System.out.println(cc2);
            }
        };
    }

    public static void main(String[] args) {
        var cc = getInstance("XXXXX");

        System.out.println();

        cc.ccc();
    }
}

class AA{


    class BB{

    }

    public static void main(String[] args) {
        var b = new AA().new BB();
        System.out.println(b);
    }
}

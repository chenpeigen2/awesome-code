package featuretest.extend;

import java.util.HashMap;
import java.util.Map;

class Base implements LifeCycleOwner {
    public String mLifecycleRegistry = new String("abc");

    public String mLifecycleRegistry3 = new String("abc");

    protected Map<String, String> headers = new HashMap<>() {
        {
            put("a", "b");
//            System.out.println(this == null);
            System.out.println(headers == null);
        }
    };

    {
        System.out.println("566666666666666");
        System.out.println(headers == null);
    }

    public String mLifecycleRegistry1 = new String("abc");

    @Override
    public String getLifeCycle() {
        return mLifecycleRegistry;
    }

    public static Base getInstance() {
        return new SUB();
    }
}


class SUB extends Base implements LifeCycleOwner {
    String mLifecycleRegistry = new String("bde");

    int a = 5678;

    public SUB() {
        super();
        System.out.println("222222222");
    }

    @Override
    public String getLifeCycle() {
        return mLifecycleRegistry;
    }

    public static void main(String[] args) {
        var app = new SUB() {{
            System.out.println(this);
            System.out.println("5567");
            System.out.println(headers == null);
        }};
//        System.out.println(app.getLifeCycle());
//        SUB sub = new SUB();
    }
}

class CC {
    protected Map<String, String> map = new HashMap<>() {{
        put("we", "are");
        map.put("s", "5");
    }};

    public static void main(String[] args) {
        new CC();
    }
}

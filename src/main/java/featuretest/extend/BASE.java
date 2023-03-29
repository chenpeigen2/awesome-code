package featuretest.extend;

class Base implements LifeCycleOwner {
    public String mLifecycleRegistry = new String("abc");

    @Override
    public String getLifeCycle() {
        return mLifecycleRegistry;
    }
}


class SUB extends Base implements LifeCycleOwner{
     String mLifecycleRegistry = new String("bde");

    @Override
    public String getLifeCycle() {
        return mLifecycleRegistry;
    }

    public static void main(String[] args) {
        var app = new SUB();
        System.out.println(app.getLifeCycle());
    }
}

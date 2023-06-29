package org.peter.utils;

public class Test {
    public static void main(String[] args) throws Throwable {
        var lz = LazyCleaner.getInstance();
        var obj = new Object();
        var aaa = lz.register(obj, new LazyCleaner.CleaningAction<Throwable>() {
            @Override
            public void onClean(boolean leak) throws Throwable {
                System.out.println("88888888888");
            }
        });

//        obj = null;


//        aaa.clean();

        while (true) {
            Thread.sleep(1000L);
        }

    }
}

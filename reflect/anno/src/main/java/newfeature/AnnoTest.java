package newfeature;

import java.lang.reflect.Modifier;

public class AnnoTest {
    public static void main(String[] args) {
//        List<@Test Comparable> list1 = new ArrayList<>();
//
//
//        List<? extends Comparable> list2 = new ArrayList<@Test Comparable>();

        @TestTYPEUSE(name = "ffff") String text;

        text = "amazing";

        Class<? extends @TestTYPEUSE(name = "ffff") String> claz = text.getClass();

        testModifier(claz.getModifiers());


        if (claz.isAnnotationPresent(TestTYPEUSE.class)) {
            TestTYPEUSE t = (TestTYPEUSE) claz.getAnnotation(TestTYPEUSE.class);
            System.out.println(t.name());
        }

        for (var a : claz.getDeclaredConstructors()) {
            if (a.isAnnotationPresent(TestTYPEUSE.class)) {
                TestTYPEUSE t = (TestTYPEUSE) a.getAnnotation(TestTYPEUSE.class);
                System.out.println(t.name());
            }
        }

        for (var a : claz.getDeclaredMethods()) {
            if (a.isAnnotationPresent(TestTYPEUSE.class)) {
                TestTYPEUSE t = (TestTYPEUSE) a.getAnnotation(TestTYPEUSE.class);
                System.out.println(t.name());
            }
        }

        for (var a : claz.getDeclaredFields()) {
            if (a.isAnnotationPresent(TestTYPEUSE.class)) {
                TestTYPEUSE t = (TestTYPEUSE) a.getAnnotation(TestTYPEUSE.class);
                System.out.println(t.name());
            }
        }

        for (var a : claz.getDeclaredClasses()) {
            if (a.isAnnotationPresent(TestTYPEUSE.class)) {
                TestTYPEUSE t = (TestTYPEUSE) a.getAnnotation(TestTYPEUSE.class);
                System.out.println(t.name());
            }
        }


        for (var a : claz.getTypeParameters()) {
            if (a.isAnnotationPresent(TestTYPEUSE.class)) {
                TestTYPEUSE t = (TestTYPEUSE) a.getAnnotation(TestTYPEUSE.class);
                System.out.println(t.name());
            }
        }

//        java.util.@Test Scanner console;
//
//        console = new java.util.@Test Scanner(System.in);


        System.out.println(claz.getModule());

        short a = 12;
    }

    public static void testModifier(int mod) {
        System.out.println("----------------------【mod=" + mod + "】----------------------");
        System.out.println("【toString】" + Modifier.toString(mod));
        System.out.println("【isPublic】" + Modifier.isPublic(mod));
        System.out.println("【isPrivate】" + Modifier.isPrivate(mod));
        System.out.println("【isProtected】" + Modifier.isProtected(mod));
        System.out.println("【isStatic】" + Modifier.isStatic(mod));
        System.out.println("【isFinal】" + Modifier.isFinal(mod));
        System.out.println("【isSynchronized】" + Modifier.isSynchronized(mod));
        System.out.println("【isVolatile】" + Modifier.isVolatile(mod));
        System.out.println("【isTransient】" + Modifier.isTransient(mod));
        System.out.println("【isNative】" + Modifier.isNative(mod));
        System.out.println("【isInterface】" + Modifier.isInterface(mod));
        System.out.println("【isAbstract】" + Modifier.isAbstract(mod));
        System.out.println("【isStrict】" + Modifier.isStrict(mod));
    }
}
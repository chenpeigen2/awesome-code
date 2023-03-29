package org.peter.basic.course;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HelloWorld extends ClassLoader {

    /**
     * 如何使用ASM动态生成一个类，并打印出HelloWorld!
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 创建一个ClassWriter来写示例类，这个类继承Object
        ClassWriter cw = new ClassWriter(0);
        /*
         * 第一个参数：JDK版本
         * 第二个参数：这个类的访问标记
         * 第三个参数：这个类的名字
         * 第四个参数：这个类的签名，当这个类没有继承或者实现一个接口的时候可以为空。
         * 第五个参数：当前类父类的名字 接口的父类是Object当该类是Object的时候为空
         * 第六个参数：接口的名字 可以为空
         */
        cw.visit(Opcodes.V18, Opcodes.ACC_PUBLIC, "Example", null, "java/lang/Object", null);
        /*
         * 创建一个写默认构造器的MethodWriter
         * 第一个参数：方法的访问标记
         * 第二个参数：方法名称
         * 第三个参数：方法的描述符号
         * 第四个参数：方法签名（当方法参数 返回类型 以及异常没有用到类属性的时候可以为空）
         * 第⑤个参数：异常名称
         */
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        // 压入this变量
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        // 执行父类构造器
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        // 这段代码使用最多一个堆元素 和一个 局部变量
        mv.visitMaxs(1, 1);
        mv.visitEnd();


        // 创建一个main方法的MethodWriter
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        // 调用System类的PrintStream类的out
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        // 压入"Hello World!" 常量
        mv.visitLdcInsn("Hello World From ASM");
        // 执行定义在PrintStream中的println方法
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        mv.visitInsn(Opcodes.RETURN);
        // 使用两个堆和两个局部变量
        mv.visitMaxs(2, 2);
        // 获取Example类的字节码并且动态加载它。


//        FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC, "a", "java/lang/String", null, null);
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "a", "Ljava/lang/String;", null, "hello world").visitEnd();

//        fv.visitEnd();


        byte[] code = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream("Example.class");
        fos.write(code);
        fos.close();

        HelloWorld loader = new HelloWorld();
        Class<?> exampleClass = loader.defineClass("Example", code, 0, code.length);
        // 使用动态生成的类打印HelloWorld
        Method method = exampleClass.getMethods()[0];

        Field f = exampleClass.getField("a");
//        System.out.println(f.get(exampleClass));
        method.invoke(null, new Object[]{null});
    }
}

class DD {
    static String a = "ddddddd";
    static int b = 234;
}

class CC {
    public static int a = 1;

    static int b = 5667788;

    static void hello(int x) {
        a = x;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        var cc = new CC();
        cc.hello(123);
        System.out.println(CC.a);
        var c1c = new CC();
        c1c.hello(555);
        System.out.println(CC.a);
        String val = (String) getStaticObjectField(DD.class, "a");
        int val1 = (int) getStaticObjectField(CC.class, "b");

        System.out.println(val);
        System.out.println(val1);
    }

    public static Object getStaticObjectField(Class<?> clazz, String field) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field declaredField = clazz.getDeclaredField(field);
        declaredField.setAccessible(true);
        return declaredField.get(null);
    }
}


class Outer {

    void hello() {
        System.out.println(this);
        System.out.println("xxxx");
    }

    public Inner inner = new Inner();

    class Inner {
        void zzz() {
            System.out.println("xxxx");
            hello();
        }
    }
}

class App {
    public static void main(String[] args) {
        Outer o = new Outer();


        var i = o.inner;

        o = null;

        System.gc();

        System.out.println(o);

        System.out.println(i);

        i.zzz();
    }
}

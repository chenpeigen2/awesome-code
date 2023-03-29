package org.peter.basic.course;

import org.objectweb.asm.Type;

public class HelloWorldRun {
    public static void main(String[] args) {
        Type t = Type.getType("Ljava/lang/String;");

        int sort = t.getSort();                    // ASM
        String className = t.getClassName();       // Java File
        String internalName = t.getInternalName(); // Class File
        String descriptor = t.getDescriptor();     // Class File

        System.out.println(sort);         // 10，它对应于Type.OBJECT字段
        System.out.println(className);    // java.lang.String   注意，分隔符是“.”
        System.out.println(internalName); // java/lang/String   注意，分隔符是“/”
        System.out.println(descriptor);   // Ljava/lang/String; 注意，分隔符是“/”，前有“L”，后有“;”
        System.out.println(Type.VOID_TYPE.getClassName());
    }




}

package org.peter.basic;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class App {

    public static void dealWithStatic(File f) throws IOException {
        ClassReader reader = new ClassReader(new FileInputStream(f));
        ClassNode node = new ClassNode(Opcodes.ASM9);
        reader.accept(node, 0);

        System.out.println();
    }

    public static void main(String[] args) throws IOException {
       File  f =  new File("/home/chenpeigen/demo_java/target/classes/lee/pkg20220606/MyCalendarThree.class");
       dealWithStatic(f);
    }
}

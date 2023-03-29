package org.peter.basic.course.core;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;

public class AnnotationScanner extends ClassVisitor {
    public static void main(String[] args) throws Exception {
        File f = new File("./input/Model.class");
        ClassReader cr = new ClassReader(new FileInputStream(f));
        cr.accept(new AnnotationScanner(), 0);
    }

    public AnnotationScanner() {
        super(Opcodes.ASM8);
    }

    static class MyAnnotationVisitor extends AnnotationVisitor {
        MyAnnotationVisitor() {
            super(Opcodes.ASM8);
        }

        @Override
        public void visit(String name, Object value) {
            System.out.println("annotation: " + name + " = " + value);
            super.visit(name, value);
        }
    }

    static class MyMethodVisitor extends MethodVisitor {
        MyMethodVisitor() {
            super(Opcodes.ASM8);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.println("annotation type: " + desc);
            return new MyAnnotationVisitor();
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        System.out.println("method: name = " + name);
        return new MyMethodVisitor();
    }
}

package org.peter.basic.utils;

import org.objectweb.asm.util.Printer;

import java.io.IOException;

/**
 * 这里的代码是参考自{@link Printer# main}
 */
public class ASMPrint {
    public static void main(String[] args) throws IOException {
//        // (1) 设置参数
//        String className = "sample.HelloWorld";
//        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
//        boolean asmCode = true;
//
//        // (2) 打印结果
//        Printer printer = asmCode ? new ASMifier() : new Textifier();
//        PrintWriter printWriter = new PrintWriter(System.out, true);
//        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
//
//        IOUtils.getClassReader(new File("./HelloWorld.class")).accept(traceClassVisitor, parsingOptions);


        System.gc();
//        System.gc();

        Runtime runtime = Runtime.getRuntime();
        System.out.println("D8 is running with total memory:" + runtime.totalMemory());
        System.out.println("D8 is running with free memory:" + runtime.freeMemory());
        System.out.println("D8 is running with max memory:" + runtime.maxMemory());
    }
}

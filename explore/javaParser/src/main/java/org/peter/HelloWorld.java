package org.peter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.XmlPrinter;
import com.github.javaparser.printer.YamlPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class HelloWorld {
    public static void main(String[] args) throws IOException {

        // 1. 转换的是完整的Java文件
//        File base = new File("");
//        String relativePath = "test-case/javaparser-testcase/src/main/java/zmj/test/thread/MyThread.java";
//        String absolutePath = base.getCanonicalPath() + File.separator + relativePath;
        ParseResult<CompilationUnit> result = new JavaParser().parse(Paths.get("/home/chenpeigen/demo_java/src/main/java/lee/pkg20220606/MyCalendarThree.java"));
        result.getResult().ifPresent(YamlPrinter::print);
    }
}
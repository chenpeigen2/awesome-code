package org.peter.shell;

import jakarta.validation.constraints.Size;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class HelloWorld {

    @ShellMethod(key = "hello-world")
    public String helloWorld(
            @ShellOption(defaultValue = "spring") String arg
    ) {
        return "Hello world " + arg;
    }

    // 为一个命令指定多个名称
    @ShellMethod(value = "Add numbers.", key = {"sum", "addition"})
    public void add(int a, int b) {
        int sum = a + b;
        System.out.printf("%d + %d = %d%n", a, b, sum);
    }

    @ShellMethod("Echo params")
    public void echo(int a, int b, @ShellOption("--third") int c) {
        System.out.printf("a=%d, b=%d, c=%d%n", a, b, c);
    }

    // 参数为一个数组
    @ShellMethod("Add by array")
    public void addByArray(@ShellOption(arity = 3) int[] numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        System.out.printf("sum=%d%n", sum);
    }

    // 参数为集合
    @ShellMethod("Add by list")
    public void addByList(@ShellOption(arity = 3) List<Integer> numbers) {
        int s = 0;
        for (int number : numbers) {
            s += number;
        }
        System.out.printf("s=%d%n", s);
    }

    // 参数为Boolean类型
    @ShellMethod("Shutdown action")
    public void shutdown(boolean shutdown) {
        System.out.printf("shutdown=%s%n", shutdown);
    }

    // 使用@Size注解校验参数长度
    @ShellMethod("Change password")
    public void changePwd(@Size(min = 6, max = 30) String pwd) {
        System.out.println(pwd);
    }
}

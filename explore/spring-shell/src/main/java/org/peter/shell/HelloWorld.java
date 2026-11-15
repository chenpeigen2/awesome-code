package org.peter.shell;

import jakarta.validation.constraints.Size;
import org.springframework.shell.core.command.annotation.Arguments;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Shell 4 的注解式命令示例。
 *
 * <p>4.0 起 {@code org.springframework.shell.standard} 包被移除：
 * {@code @ShellComponent}/{@code @ShellMethod}/{@code @ShellOption} 全部废弃，
 * 改为在普通 Spring bean 上用 {@code @Command}/{@code @Option}/{@code @Argument} 声明命令。
 */
@Component
@CommandGroup(name = "hello", description = "Hello world 示例命令")
public class HelloWorld {

    @Command(name = "hello-world", description = "Say hello")
    public String helloWorld(@Option(longName = "arg", defaultValue = "spring") String arg) {
        return "Hello world " + arg;
    }

    // 为一个命令指定多个名称
    @Command(name = "sum", alias = "addition", description = "Add numbers.")
    public String add(@Option(longName = "a") int a, @Option(longName = "b") int b) {
        return "%d + %d = %d".formatted(a, b, a + b);
    }

    @Command(name = "echo", description = "Echo params")
    public String echo(@Option(longName = "a") int a,
                       @Option(longName = "b") int b,
                       @Option(longName = "third") int c) {
        return "a=%d, b=%d, c=%d".formatted(a, b, c);
    }

    // 参数为一个数组
    @Command(name = "add-by-array", description = "Add by array")
    public String addByArray(@Arguments(arity = 3) int[] numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return "sum=" + sum;
    }

    // 参数为集合
    // Spring Shell 4 对 List 的元素不做类型转换，元素按原始字符串传入，
    // 所以这里声明为 List<String> 再自行解析。
    @Command(name = "add-by-list", description = "Add by list")
    public String addByList(@Arguments(arity = 3) List<String> numbers) {
        int s = 0;
        for (String number : numbers) {
            s += Integer.parseInt(number);
        }
        return "s=" + s;
    }

    // 参数为Boolean类型
    @Command(name = "shutdown-app", description = "Shutdown action")
    public String shutdown(@Option(longName = "shutdown") boolean shutdown) {
        return "shutdown=" + shutdown;
    }

    // 使用@Size注解校验参数长度
    @Command(name = "change-pwd", description = "Change password")
    public String changePwd(@Option(longName = "pwd") @Size(min = 6, max = 30) String pwd) {
        return pwd;
    }
}

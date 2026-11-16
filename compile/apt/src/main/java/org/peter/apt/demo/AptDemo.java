package org.peter.apt.demo;

/**
 * 注解处理器演示：{@code PersonBuilder} 这个类在源码里不存在，
 * 是 {@code BuilderProcessor} 在编译期生成的，这里直接用。
 * <p>
 * 运行：{@code ./gradlew :compile:apt:demo}
 *
 * <p>能编译通过本身就是最强的证据：如果处理器没跑，这里会报
 * {@code cannot find symbol: class PersonBuilder}。
 */
public final class AptDemo {

    public static void main(String[] args) {
        System.out.println(new AptDemo().run());
    }

    /** 跑一遍并返回可读报告（测试直接断言这份报告）。 */
    public String run() {
        Person person = new PersonBuilder()
                .setName("张三")
                .setAge(30)
                .setEmail("zhangsan@example.com")
                .build();

        StringBuilder out = new StringBuilder();
        out.append("PersonBuilder was generated at compile time\n");
        out.append("built: ").append(person).append('\n');
        out.append("getName(): ").append(person.getName()).append('\n');
        out.append("getAge(): ").append(person.getAge()).append('\n');
        out.append("chaining works: ").append(
                new PersonBuilder().setName("a").setAge(1).setEmail("b").build().getName()
                        .equals("a"));
        return out.toString();
    }
}

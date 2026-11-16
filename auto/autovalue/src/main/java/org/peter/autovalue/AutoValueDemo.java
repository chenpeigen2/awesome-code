package org.peter.autovalue;

/**
 * 演示入口：两种值对象写法各跑一遍。
 * <p>
 * 运行：{@code ./gradlew :auto:autovalue:demo}
 */
public final class AutoValueDemo {

    public static void main(String[] args) {
        System.out.print(new AutoValueDemo().run());
    }

    /** 返回可读报告；测试直接断言这份报告，避免 demo 静默失效。 */
    public String run() {
        Person person = Person.create("张三", 30);
        Money money = Money.builder().currency("CNY").amountInCents(1999).build();

        StringBuilder out = new StringBuilder();
        out.append("AutoValue generates the implementations at compile time\n");
        out.append("factory: ").append(person).append('\n');
        out.append("builder: ").append(money).append('\n');
        out.append("equals/hashCode are generated: ")
                .append(Person.create("张三", 30).equals(person))
                .append('\n');
        out.append("validation in build(): ");
        try {
            Money.builder().currency("CNY").amountInCents(-1).build();
            out.append("NOT enforced (unexpected)");
        } catch (IllegalArgumentException e) {
            out.append("rejected negative amount");
        }
        return out.toString();
    }
}

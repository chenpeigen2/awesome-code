package org.peter.autovalue;

import com.google.auto.value.AutoValue;

/**
 * 带 Builder 的值对象，并且带构造校验。
 * <p>
 * 属性较多时工厂方法的参数列表会变成一长串位置参数，调用处看不出含义；
 * Builder 让每个参数自带名字。{@code @AutoValue.Builder} 会生成
 * {@code AutoValue_Money.Builder}，与 {@link Builder} 抽象类对接。
 */
@AutoValue
public abstract class Money {

    public abstract String currency();

    public abstract long amountInCents();

    public static Money of(String currency, long amountInCents) {
        return builder().currency(currency).amountInCents(amountInCents).build();
    }

    public static Builder builder() {
        return new AutoValue_Money.Builder();
    }

    /**
     * 抽象 Builder：AutoValue 生成的实现会在这里填上每个 setter 的方法体。
     * <p>
     * 校验放在 {@link #build()} 而不是 setter 里，是为了让「部分构建」的中间状态
     * 始终合法——setter 只负责收集值，{@code build()} 才是承诺。
     */
    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder currency(String currency);

        public abstract Builder amountInCents(long amountInCents);

        abstract Money autoBuild();

        public Money build() {
            Money money = autoBuild();
            if (money.amountInCents() < 0) {
                throw new IllegalArgumentException(
                        "amountInCents must be non-negative but was " + money.amountInCents());
            }
            return money;
        }
    }
}

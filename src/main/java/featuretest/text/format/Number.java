package featuretest.text.format;

import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Number {
    public static void main(String[] args) {
        double number = 12345.6789;

        // 获取默认格式的数字格式化程序
        NumberFormat defaultFormat = NumberFormat.getInstance();

        // 格式化数字
        String defaultFormattedNumber = defaultFormat.format(number);
        System.out.println("Default format: " + defaultFormattedNumber);

        // 获取货币格式的数字格式化程序
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // 格式化货币
        String currencyFormattedNumber = currencyFormat.format(number);
        System.out.println("Currency format: " + currencyFormattedNumber);

        // 获取百分比格式的数字格式化程序
        NumberFormat percentFormat = NumberFormat.getPercentInstance();

        // 格式化百分比
        String percentFormattedNumber = percentFormat.format(number);
        System.out.println("Percent format: " + percentFormattedNumber);


//        CompactNumberFormat是Java中的一个类，用于将数字格式化为紧凑形式。
        number = 10000;
        Locale locale = Locale.US;
        NumberFormat formatter = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.SHORT);
        String formattedNumber = formatter.format(number);
        System.out.println("Formatted number: " + formattedNumber);


//        ChoiceFormat是Java中的一个类，用于将一组数字映射到一组字符串。
        double[] limits = {0, 1, 2};
        String[] formats = {"zero", "one", "other"};
        ChoiceFormat ChoiceFormatter = new ChoiceFormat(limits, formats);
        System.out.println(ChoiceFormatter.format(0)); // zero
        System.out.println(ChoiceFormatter.format(1)); // one
        System.out.println(ChoiceFormatter.format(2)); // other


        double[] limits1 = {0, 1, 2};
        String[] formats1 = {"zero", "one", "between {0,number,#.#} and {1,number,#.#}"};
        ChoiceFormat formatter1 = new ChoiceFormat(limits1, formats1);
        System.out.println(formatter1.format(1.5)); // between 1.0 and 2.0

    }
}

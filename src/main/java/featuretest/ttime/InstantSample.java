package featuretest.ttime;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class InstantSample {

    public static void main(String[] args) {
        System.out.println(Instant.now());

        //将java.util.Date转换为Instant
        Instant instant = Instant.ofEpochMilli(new Date().getTime());

        System.out.println(instant);
        System.out.println(instant.plusSeconds(60));
//从字符串类型中创建Instant类型的时间
        instant = Instant.parse("1995-10-23T10:12:35Z");
        System.out.println(instant);

        instant.plus(Duration.ofHours(5).plusMinutes(4));


        //计算5天前的时间
        instant.minus(5, ChronoUnit.DAYS); // Option 1　方法1
        instant.minus(Duration.ofDays(5)); // Option 2  方法2

//计算两个Instant之间的分钟数
        Instant instant1 = Instant.now();
        long diffAsMinutes = ChronoUnit.MINUTES.between(instant, instant1); // 方法2


        //用compareTo方法比较
        System.out.format("instant1.compareTo(instant)=%d.%n", instant1.compareTo(instant));

// 使用isAfter()和isBefore()
        System.out.format("instant1.isAfter(instant)=%b, instant1.isBefore(instant)=%b.%n",
                instant1.isAfter(instant), instant1.isBefore(instant));
    }
}

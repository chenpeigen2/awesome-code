package featuretest.ttime;

import java.time.*;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class LocalSample {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
        localDate = LocalDate.ofYearDay(2005, 86); // 获得2005年的第86天 (27-Mar-2005)
        localDate = LocalDate.of(2013, Month.AUGUST, 10); //2013年8月10日

        System.out.println(localDate);

        LocalTime localTime = LocalTime.of(22, 33); //10:33 PM
        localTime = LocalTime.now();
        System.out.println(localTime);
        localTime = LocalTime.ofSecondOfDay(4503); // 返回一天中的第4503秒 (1:15:30 AM)


        LocalDateTime localDateTime = LocalDateTime.now();
//当前时间加上25小时３分钟
        LocalDateTime inTheFuture = localDateTime.plusHours(25).plusMinutes(3);
// 同样也可以用在localTime和localDate中
        System.out.println(localDateTime.toLocalTime().plusHours(25).plusMinutes(3));
        System.out.println(localDateTime.toLocalDate().plusMonths(2));
// 也可以使用实现TemportalAmount接口的Duration类和Period类
        System.out.println(localDateTime.toLocalTime().plus(Duration.ofHours(25).plusMinutes(3)));
        System.out.println(localDateTime.toLocalDate().plus(Period.ofMonths(2)));
    }
}

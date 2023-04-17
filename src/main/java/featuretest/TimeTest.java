package featuretest;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeTest {
    // DateFormat TimeZone Calendar

    //    Package java.text java.util
    public static void main(String[] args) throws InterruptedException {

        testCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String str3 = "1900-01-01 08:05:42";
        String str4 = "1900-01-01 08:05:43";
        Date st3 = new Date();
        Thread.sleep(1000);
        Date st4 = new Date();

        System.out.println(sdf.getTimeZone().toString());

        System.out.println(sdf.format(st3)
        );
        long ld3 = st3.getTime() / 1000;
        long ld4 = st4.getTime() / 1000;
        System.out.println(ld4 - ld3);


        String[] ids = TimeZone.getAvailableIDs();
//        Arrays.stream(ids).forEach(System.out::println);

        jdk8time();
    }

    public static void testCalendar() {
        System.out.println(System.currentTimeMillis());
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, 1);

        System.out.println(calendar.getTime());
    }


//  1  java.time
//    The main API for dates, times, instants, and durations.
//    java.time.chrono
//    Generic API for calendar systems other than the default ISO.
//  2          java.time.format
//    Provides classes to print and parse dates and times.
//  3         java.time.temporal
//    Access to date and time using fields and units, and date time adjusters.
//  4   java.time.zone
//    Support for time-zones and their rules.

    static void jdk8time() {

        // 01:获取当前时间
        LocalDate now = LocalDate.now();
        System.out.println("当前时间:" + now);

        // 02:自定义当前时间
        LocalDate data = LocalDate.of(1997, 02, 21);
        System.out.println("自定义当前时间:" + data);

        // 03:修改时间的格式
        String dataWithString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("当前时间的String格式:" + dataWithString);

        // 04:解析当前时间
        LocalDate parseTime = LocalDate.parse("1997-02-21");
        System.out.println("解析当前的时间" + parseTime);

        // 05:获取今天是本月的第几天
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        System.out.println("dateOfMonth:" + dayOfMonth);

        // 06:获取今天是今年的第多少天
        int dateOfYear = LocalDate.now().getDayOfYear();
        System.out.println("dateOfYear:" + dateOfYear);

        // 07:获取年
        int year = LocalDate.now().getYear();
        System.out.println("year:" + year);

        // 08:获取星期
        DayOfWeek week = LocalDate.now().getDayOfWeek();
        System.out.println(week.getValue());

        // 09:获取当前时间并给它加一定的天数
        LocalDate localDate = LocalDate.now().plusDays(5);
        System.out.println("添加一定天数后的时间:" + localDate);

        // 10:获取当前时间并给它减一定的天数
        LocalDate minusDate = LocalDate.now().minusDays(9);
        System.out.println("减去一定天数后的时间:" + minusDate);

        // 11:计算两个日期间的天数
        long dates = localDate.until(minusDate, ChronoUnit.DAYS);
        System.out.println("相差的天数:" + dates);

        // 12:计算两个日期间的天数的周数
        long until = localDate.until(minusDate, ChronoUnit.WEEKS);
        System.out.println("相差的周数:" + until);


        zoneTime();
    }

    static void zoneTime() {


        ZoneId zoneId = ZoneId.of("UTC+7");

        ZonedDateTime zd = ZonedDateTime.now(zoneId);
        System.out.println(zd);

        // ok we get it
        var zd1 = ZonedDateTime.of(LocalDateTime.now(), zoneId);

        System.out.println(zd1);
    }
}

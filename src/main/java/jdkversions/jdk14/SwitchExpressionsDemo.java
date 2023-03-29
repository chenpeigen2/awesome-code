package jdkversions.jdk14;


import java.util.Calendar;


public class SwitchExpressionsDemo {
    public static void main(String[] args) {

        int day = Calendar.MONDAY;
        switch (day) {
            case Calendar.MONDAY, Calendar.FRIDAY, Calendar.SUNDAY -> System.out.println(6);
            case Calendar.TUESDAY -> System.out.println(7);
            case Calendar.THURSDAY, Calendar.SATURDAY -> System.out.println(8);
            case Calendar.WEDNESDAY -> System.out.println(9);
        }
    }
}

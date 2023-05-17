package lee.pkg20230517;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Solution {
    public static void main(String[] args) {
        String humanTime = "02:00";
        DateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date date = format.parse(humanTime);
            System.out.println(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean haveConflict(String[] event1, String[] event2) {
        DateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date a = format.parse(event1[0]); // 11
            Date b = format.parse(event1[1]); // 13

            Date c = format.parse(event2[0]); // 14
            Date d = format.parse(event2[1]); // 10

            if (c.getTime() > b.getTime() || a.getTime() > d.getTime()) return false;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean haveConflict1(String[] event1, String[] event2) {
        String a = event1[0];
        String b = event1[1];

        String c = event2[0];
        String d = event2[1];

        if (c.compareTo(b) > 0 || a.compareTo(b) > 0) return false;
        return true;
    }
}

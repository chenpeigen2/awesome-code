package lee.pkg20220103;

import java.util.Calendar;
import java.util.Date;

public class Solution {
    public String dayOfTheWeek(int day, int month, int year) {
        String[] weeks = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(year - 1900, month - 1, day));
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weeks[w];
    }
}

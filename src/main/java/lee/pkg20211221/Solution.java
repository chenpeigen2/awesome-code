package lee.pkg20211221;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Solution {
    private static final DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public int dayOfYear(String date) {
        // LocalDate  DateTimeFormatter
        return LocalDate.parse(date, d).getDayOfYear();
    }
}

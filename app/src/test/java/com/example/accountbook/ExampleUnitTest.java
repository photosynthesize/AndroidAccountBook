package com.example.accountbook;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void showDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        String date = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        System.out.println(date);
    }

    @Test
    public String[] DateInAWeek() {
        int[] daysOfWeek = {7, 1, 2, 3, 4, 5, 6};
        Calendar calendar = Calendar.getInstance();
        int dayEurope = calendar.get(Calendar.DAY_OF_WEEK);
        int dayChinese = daysOfWeek[dayEurope - 1];
        String[] res = new String[dayChinese];
        for (int i = dayChinese; i >= 1; i--) {
            res[i - 1] = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            calendar.add(Calendar.DATE, -1);
        }
        return res;
    }
}
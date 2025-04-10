
package io.github.anthonyclemens.Logic;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class CalenderTest {

    @Test
    public void testInitialDate() {
        Calender calender = new Calender(1, 1, 2023);
        assertEquals("January 01, 2023", calender.toString());
    }

    @Test
    public void testIncrementDayWithinSameMonth() {
        Calender calender = new Calender(1, 1, 2023);
        calender.incrementDay();
        assertEquals("January 02, 2023", calender.toString());
    }

    @Test
    public void testIncrementDayEndOfMonth() {
        Calender calender = new Calender(31, 1, 2023);
        calender.incrementDay();
        assertEquals("February 01, 2023", calender.toString());
    }

    @Test
    public void testIncrementDayEndOfYear() {
        Calender calender = new Calender(31, 12, 2023);
        calender.incrementDay();
        assertEquals("January 01, 2024", calender.toString());
    }

    @Test
    public void testLeapYearFebruary() {
        Calender calender = new Calender(28, 2, 2024); // 2024 is a leap year
        calender.incrementDay();
        assertEquals("February 29, 2024", calender.toString());
        calender.incrementDay();
        assertEquals("March 01, 2024", calender.toString());
    }

    @Test
    public void testNonLeapYearFebruary() {
        Calender calender = new Calender(28, 2, 2023); // 2023 is not a leap year
        calender.incrementDay();
        assertEquals("March 01, 2023", calender.toString());
    }

    @Test
    public void testMonthWith30Days() {
        Calender calender = new Calender(30, 4, 2023); // April has 30 days
        calender.incrementDay();
        assertEquals("May 01, 2023", calender.toString());
    }

    @Test
    public void testMonthWith31Days() {
        Calender calender = new Calender(31, 7, 2023); // July has 31 days
        calender.incrementDay();
        assertEquals("August 01, 2023", calender.toString());
    }
}
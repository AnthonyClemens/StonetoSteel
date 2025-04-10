package io.github.anthonyclemens.Logic;

import java.io.Serializable;

public class Calender implements Serializable{
    private int day;
    private int month;
    private int year;
    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public Calender(int startDay, int startMonth, int startYear) {
        this.day = startDay;
        this.month = startMonth;
        this.year = startYear;
    }

    public void incrementDay() {
        day++;

        // Check if day exceeds the number of days in the current month
        if (day > getDaysInMonth(month, year)) {
            day = 1; // Reset day
            month++; // Increment month

            // Check if month exceeds 12 (December)
            if (month > 12) {
                month = 1; // Reset month to January
                year++; // Increment year
            }
        }
    }

    private int getDaysInMonth(int month, int year) {
        return switch (month) {
            case 2 -> isLeapYear(year) ? 29 : 28; // February
            case 4, 6, 9, 11 -> 30; // Months with 30 days
            default -> 31; // Months with 31 days
        };
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    @Override
    public String toString() {
        String monthName = MONTH_NAMES[month - 1];
        return String.format("%s %02d, %04d", monthName, day, year);
    }
}

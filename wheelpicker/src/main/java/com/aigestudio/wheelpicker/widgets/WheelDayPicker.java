package com.aigestudio.wheelpicker.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.aigestudio.wheelpicker.WheelPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WheelDayPicker extends WheelPicker implements IWheelDayPicker {
    private static final Map<Integer, List<Integer>> DAYS = new HashMap<>();

    private Calendar mCalendar;

    private int mYear, mMonth;
    private int mSelectedDay;
    private String selectedMonth;
    private String selectedYear;

    public WheelDayPicker(Context context) {
        this(context, null, "", "");
    }

    public WheelDayPicker(Context context, AttributeSet attrs, String selectedMonth, String selectedYear) {
        super(context, attrs, selectedMonth, selectedYear);

        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;

        mCalendar = Calendar.getInstance();

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);

        try {
            updateDays(selectedMonth, selectedYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mSelectedDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        updateSelectedDay();
    }

    private void updateDays(String selectedMonth, String selectedYear) throws ParseException {
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);

        int days = 0;

        if (selectedMonth.length() > 0) {
            mCalendar.set(Calendar.MONTH, Integer.parseInt(changeMonthStringToNumberFormat(selectedMonth)));
            days = getMonthDays(Integer.parseInt(changeMonthStringToNumberFormat(selectedMonth)), Integer.parseInt(selectedYear));
        } else if (selectedYear.length() > 0) {
            mCalendar.set(Calendar.YEAR, Integer.parseInt(selectedYear));
            days = getMonthDays(Integer.parseInt(changeMonthStringToNumberFormat(selectedMonth)), Integer.parseInt(selectedYear));
        } else
            days = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<Integer> data = DAYS.get(days);
        if (null == data) {
            data = new ArrayList<>();
            for (int i = 1; i <= days; i++)
                data.add(i);
            DAYS.put(days, data);
        }
        super.setData(data);
    }

    private String changeMonthStringToNumberFormat(String selectedMonth) throws ParseException {

        SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM", Locale.US);
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(inputFormat.parse(selectedMonth));
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM", Locale.US);

        return outputFormat.format(calendar.getTime());
    }

    public static int getMonthDays(int month, int year) {
        int daysInMonth ;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        }
        else {
            if (month == 2) {
                daysInMonth = (year % 4 == 0) ? 29 : 28;
            } else {
                daysInMonth = 31;
            }
        }
        return daysInMonth;
    }

    private void updateSelectedDay() {
        setSelectedItemPosition(mSelectedDay - 1);
    }

    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You can not invoke setData in WheelDayPicker");
    }

    @Override
    public int getSelectedDay() {
        return mSelectedDay;
    }

    @Override
    public void setSelectedDay(int day) {
        mSelectedDay = day;
        updateSelectedDay();
    }

    @Override
    public int getCurrentDay() {
        return Integer.valueOf(String.valueOf(getData().get(getCurrentItemPosition())));
    }

    @Override
    public void setYearAndMonth(int year, int month) {
        mYear = year;
        mMonth = month - 1;
        try {
            updateDays(selectedMonth, selectedYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getYear() {
        return mYear;
    }

    @Override
    public void setYear(int year) {
        mYear = year;
        try {
            updateDays(selectedMonth, selectedYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getMonth() {
        return mMonth;
    }

    @Override
    public void setMonth(int month) {
        mMonth = month - 1;
        try {
            updateDays(selectedMonth, selectedYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
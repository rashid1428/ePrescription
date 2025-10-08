package com.aigestudio.wheelpicker.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.aigestudio.wheelpicker.WheelPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WheelYearPicker extends WheelPicker implements IWheelYearPicker {
    private int minimumYear = 1872, currentYear;
    private int mSelectedYear;
    private String selectedYear;

    public WheelYearPicker(Context context) {
        this(context, null, "", "");
    }

    public WheelYearPicker(Context context, AttributeSet attrs, String selectedYear, String selectedMonth) {
        super(context, attrs);

        this.selectedYear = selectedYear;
//        mSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
        mSelectedYear = 2121;
        updateYears();
        if (selectedYear.equals("")) this.selectedYear = String.valueOf(mSelectedYear);
        updateSelectedYear();
    }

    private void updateYears() {
        String currentDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        int currentYear = Integer.parseInt(currentDate.substring(6));

        List<Integer> data = new ArrayList<>();
        /*for (int i = currentYear; i >= minimumYear; i--)
            data.add(i);*/
        for (int i = minimumYear; i <= mSelectedYear; i++)
            data.add(i);
        super.setData(data);
    }

    private void updateSelectedYear() {
        setSelectedItemPosition(mSelectedYear - Integer.parseInt(selectedYear));
    }

    @Override
    public void setData(List data) {
        throw new UnsupportedOperationException("You cannot invoke setData in WheelYearPicker");
    }

    @Override
    public void setYearFrame(int start, int end) {
        currentYear = start;
        minimumYear = end;
        mSelectedYear = getCurrentYear();
        updateYears();
        updateSelectedYear();
    }

    @Override
    public int getYearStart() {
        return currentYear;
    }

    @Override
    public void setYearStart(int start) {
        currentYear = start;
        mSelectedYear = getCurrentYear();
        updateYears();
        updateSelectedYear();
    }

    @Override
    public int getYearEnd() {
        return minimumYear;
    }

    @Override
    public void setYearEnd(int end) {
        minimumYear = end;
        updateYears();
    }

    @Override
    public int getSelectedYear() {
        return mSelectedYear;
    }

    @Override
    public void setSelectedYear(int year) {
        mSelectedYear = year;
        updateSelectedYear();
    }

    @Override
    public int getCurrentYear() {
        return Integer.parseInt(String.valueOf(getData().get(getCurrentItemPosition())));
    }
}

package com.aigestudio.wheelpicker;

import android.graphics.Typeface;

public class RootValues {
    private static Typeface montserratSemiBoldTypeface;
    private static Typeface montserratBoldTypeface;
    private static Typeface montserratRegularTypeface;

    static void setMontserratRegularFont(Typeface montserratRegularTypeface) {
        RootValues.montserratRegularTypeface = montserratRegularTypeface;
    }

    static Typeface getMontserratRegularFont() {
        return montserratRegularTypeface;
    }

    static void setMontserratBoldFont(Typeface montserratBoldTypeface) {
        RootValues.montserratBoldTypeface = montserratBoldTypeface;
    }

    static Typeface getMontserratBoldFont() {
        return montserratBoldTypeface;
    }

    static void setMontserratSemiBoldFont(Typeface montserratSemiBoldTypeface) {
        RootValues.montserratSemiBoldTypeface = montserratSemiBoldTypeface;
    }

    Typeface getMontserratSemiBoldFont() {
        return montserratSemiBoldTypeface;
    }
}

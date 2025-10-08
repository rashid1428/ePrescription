package com.doctor.eprescription.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.aigestudio.wheelpicker.WheelPicker
import com.aigestudio.wheelpicker.widgets.WheelDayPicker
import com.aigestudio.wheelpicker.widgets.WheelMonthPicker
import com.aigestudio.wheelpicker.widgets.WheelYearPicker
import com.doctor.eprescription.R

@SuppressLint("InflateParams")
fun showDatePickerDialog(
    activity: Activity,
    selectedDay: String,
    selectedMonth: String,
    selectedYear: String,
    onDoneClick: (day: String, month: String, year: String) -> Unit,
    onCancel: () -> Unit
) {
    try {
        if (activity.isFinishing) return

        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.date_picker, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (alertDialog.isShowing) {
            return
        }
        alertDialog.show()

        val monthWheel = dialogView.findViewById(R.id.monthWheel) as WheelPicker
        val dayWheel = dialogView.findViewById(R.id.dayWheel) as WheelPicker
        val yearWheel = dialogView.findViewById(R.id.yearWheel) as WheelPicker

        val cancelButton = dialogView.findViewById(R.id.cancelButton) as TextView
//        cancelButton.typeface = getMontserratSemiBoldFont()
        val doneButton = dialogView.findViewById(R.id.doneButton) as TextView
//        doneButton.typeface = getMontserratSemiBoldFont()

        val wheelYearPicker = WheelYearPicker(activity, null, selectedYear, "")
        val wheelDayPicker = WheelDayPicker(activity, null, "", "")
        val wheelMonthPicker = WheelMonthPicker(activity, null)

        //set data in wheel picker
        dayWheel.data = wheelDayPicker.data
        yearWheel.data = wheelYearPicker.data
        monthWheel.data = wheelMonthPicker.data

        //get current day
        dayWheel.postDelayed({
            if (hasValue(selectedDay)) dayWheel.setSelectedItemPosition(
                selectedDay.toInt() - 1,
                false
            )
            else dayWheel.setSelectedItemPosition(wheelDayPicker.selectedDay - 1, false)
        }, 10)

        //get current month
        monthWheel.postDelayed({
            if (hasValue(selectedMonth)) monthWheel.setSelectedItemPosition(
                selectedMonth.toInt(),
                false
            )
            else monthWheel.setSelectedItemPosition(wheelMonthPicker.selectedMonth - 1, false)
        }, 10)

        //get current year
        yearWheel.postDelayed({
            if (hasValue(selectedYear)) {
                //lowest year subtracted with highest year gives us 249 which we will substract from highest year
                //from current year to give us a number and that will be subtracted further with the 249 to give us the index
                val yearSelectedIndex = 249 - (wheelYearPicker.selectedYear - selectedYear.toInt())
                yearWheel.setSelectedItemPosition(
                    yearSelectedIndex, false
                )
            } else yearWheel.setSelectedItemPosition(
                wheelYearPicker.selectedYear - wheelYearPicker.selectedYear,
                false
            )
        }, 10)

        monthWheel.setOnItemSelectedListener { picker, data, position ->
            val mWheelDayPicker: WheelDayPicker?
            mWheelDayPicker = WheelDayPicker(
                activity, null, data.toString(),
                yearWheel.data[yearWheel.currentItemPosition].toString()
            )
            dayWheel.data = mWheelDayPicker.data
        }

        yearWheel.setOnItemSelectedListener { picker, data, position ->
            val mWheelYearPicker: WheelYearPicker?
            mWheelYearPicker = WheelYearPicker(
                activity, null,
                yearWheel.data[yearWheel.currentItemPosition].toString(), data.toString()
            )
            yearWheel.data = mWheelYearPicker?.data

//            Log.e("year value: ", yearWheel.data[yearWheel.currentItemPosition].toString())
//            Log.e("year value1: ", yearWheel.data.toString())

            //when user scrolls below 1903, animate the date back to 1903
            //31 is the index for 1903

            if (yearWheel.currentItemPosition.toString().toInt() < 31) {
//                yearWheel.scrollTo(0, 31)
                yearWheel.setSelectedItemPosition(31, false)
            }
        }

        cancelButton.setOnClickListener {
            onCancel()
            alertDialog.dismiss()
        }

        doneButton.setOnClickListener {
            //incase user presses done button when the selected value is less than 1903
            if (yearWheel.currentItemPosition.toString().toInt() >= 31) {
                onDoneClick(
                    dayWheel.data[dayWheel.currentItemPosition].toString(),
                    monthWheel.currentItemPosition.toString(),
                    yearWheel.data[yearWheel.currentItemPosition].toString()
                )

                alertDialog.dismiss()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }//catch ends
}//showDatePickerDialog ends
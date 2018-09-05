package com.freshlancers.sriambalcrusher.utils;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;

import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by Ashith VL on 10/21/2017.
 */

public class CustomDatePicker {

    public static DialogFragment createDialog(final TextInputEditText mDateEditText) {
        BottomSheetPickerDialog.Builder builder = null;
        Calendar now = Calendar.getInstance();
       // now.set(Calendar.YEAR, -10);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.YEAR, 10);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.YEAR, -10);

        builder = new DatePickerDialog.Builder(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                        int formattedMonth = monthOfYear+1;
                        mDateEditText.setText(dayOfMonth + " - " + formattedMonth + " - " + year);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.Builder dateDialogBuilder = (DatePickerDialog.Builder) builder;

        dateDialogBuilder
                .setMaxDate(max)
                .setMinDate(min)
                .setYearRange(1970, 2032);

        dateDialogBuilder.setHeaderTextColorSelected(0xFFFF4081)
                .setHeaderTextColorUnselected(0x4AFF4081)
                .setDayOfWeekHeaderTextColorSelected(0xFFFF4081)
                .setDayOfWeekHeaderTextColorUnselected(0x4AFF4081);

        builder.setThemeDark(true);

        return builder.build();
    }

}

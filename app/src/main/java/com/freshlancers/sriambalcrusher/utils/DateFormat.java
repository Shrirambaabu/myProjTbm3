package com.freshlancers.sriambalcrusher.utils;

import android.support.design.widget.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shriram on 14-Nov-17.
 */

public class DateFormat {


    public static String dayToYear(String mDateEditText) {

        String yearOut=null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd - MM - yyyy");
        try {

            Date dateInput = dateFormat.parse(mDateEditText);
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd");

            yearOut=yearFormat.format(dateInput);


        } catch (ParseException e) {
            e.printStackTrace();

        }


        return yearOut;
    }
}

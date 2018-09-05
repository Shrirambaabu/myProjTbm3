package com.freshlancers.sriambalcrusher.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.utils.InputValidation;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.freshlancers.sriambalcrusher.R.id.chart;
import static com.freshlancers.sriambalcrusher.utils.Constants.COLLECTION_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.EXPENSE_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.CustomDatePicker.createDialog;
import static com.freshlancers.sriambalcrusher.utils.DateFormat.dayToYear;

/**
 * Created by Ashith VL on 10/20/2017.
 */

public class ExpenseFragment extends Fragment {

    private static final String TAG = "ExpenseFragment";
    private Context mCONTEXT;
    private HorizontalBarChart mHorizontalBarChart;
    private TextInputEditText mDateEditFromText, mDateEditToText;
    private TextInputLayout mDateEditFromTextLayout, mDateEditToTextLayout;
    private InputValidation inputValidation;
    private StringRequest request;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        mCONTEXT = getActivity();
        mHorizontalBarChart = view.findViewById(chart);
        AppCompatButton submit = view.findViewById(R.id.appCompatButtonSubmit);
        mDateEditFromTextLayout = view.findViewById(R.id.textInputLayoutFromDate);
        mDateEditToTextLayout = view.findViewById(R.id.textInputLayoutToDate);
        mDateEditFromText = view.findViewById(R.id.textInputEditTextFromDate);
        relativeLayout = view.findViewById(R.id.rel_relative2);
        relativeLayout.setVisibility(View.GONE);
        inputValidation = new InputValidation(mCONTEXT);
        mDateEditFromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = createDialog(mDateEditFromText);
                dialog.show(getActivity().getSupportFragmentManager(), TAG);
            }
        });

        mDateEditToText = view.findViewById(R.id.textInputEditTextToDate);
        mDateEditToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = createDialog(mDateEditToText);
                dialog.show(getActivity().getSupportFragmentManager(), TAG);
            }
        });

        ///  materialSpinner(view);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fromDate = dayToYear(mDateEditFromText.getText().toString());
                String toDate = dayToYear(mDateEditToText.getText().toString());

                if (!inputValidation.isInputEditTextFieldValidation(mDateEditFromText, mDateEditFromTextLayout, getString(R.string.error_message_date))) {
                    return;
                }
                if (!inputValidation.isInputEditTextFieldValidation(mDateEditToText, mDateEditToTextLayout, getString(R.string.error_message_date))) {
                    return;
                }
                postRequest(fromDate, toDate);

                AppController.getInstance().addToRequestQueue(request, TAG);
            }
        });

        return view;
    }

    private void postRequest(final String fromDate, final String toDate) {

        request = new StringRequest(Request.Method.POST, EXPENSE_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                handleResponse(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onErrorResponse: " + volleyError.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("from", fromDate);
                parameters.put("to", toDate);

                return parameters;
            }
        };

    }

    private void handleResponse(String result) {


        String salaryAmount, purchaseAmount, employeeAmount, miscellaneousAmount, materialAmount, explosiveAmount, fuelAmount;

        try {
            JSONArray jsonArray = new JSONArray(result);

            JSONObject salaryAmountObject = jsonArray.getJSONObject(0);
            salaryAmount = salaryAmountObject.getString("amount");

            JSONObject purchaseAmountObject = jsonArray.getJSONObject(1);
            purchaseAmount = purchaseAmountObject.getString("amount");

            JSONObject employeeAmountObject = jsonArray.getJSONObject(2);
            employeeAmount = employeeAmountObject.getString("amount");

            JSONObject miscellaneousAmountObject = jsonArray.getJSONObject(3);
            miscellaneousAmount = miscellaneousAmountObject.getString("amount");

            JSONObject materialAmountObject = jsonArray.getJSONObject(4);
            materialAmount = materialAmountObject.getString("amount");

            JSONObject explosiveAmountObject = jsonArray.getJSONObject(5);
            explosiveAmount = explosiveAmountObject.getString("amount");

            JSONObject fuelAmountObject = jsonArray.getJSONObject(6);
            fuelAmount = fuelAmountObject.getString("amount");

            if (salaryAmount.equals("null") && purchaseAmount.equals("null") && employeeAmount.equals("null") && miscellaneousAmount.equals("null") && materialAmount.equals("null") && explosiveAmount.equals("null") && fuelAmount.equals("null")) {
                relativeLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_LONG).show();
            } else {
                if (salaryAmount.equals("null")) {
                    salaryAmount = "0";
                }
                if (purchaseAmount.equals("null")) {
                    purchaseAmount = "0";
                }
                if (employeeAmount.equals("null")) {
                    employeeAmount = "0";
                }
                if (miscellaneousAmount.equals("null")) {
                    miscellaneousAmount = "0";
                }
                if (materialAmount.equals("null")) {
                    materialAmount = "0";
                }
                if (explosiveAmount.equals("null")) {
                    explosiveAmount = "0";
                }
                if (fuelAmount.equals("null")) {
                    fuelAmount = "0";
                }

                horizontalBarDataSet(salaryAmount, purchaseAmount, employeeAmount, miscellaneousAmount, materialAmount, explosiveAmount, fuelAmount);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void horizontalBarDataSet(String salaryAmount, String purchaseAmount, String employeeAmount, String miscellaneousAmount, String materialAmount, String explosiveAmount, String fuelAmount) {

        BarData data = new BarData(getDataSet(salaryAmount, purchaseAmount, employeeAmount, miscellaneousAmount, materialAmount, explosiveAmount, fuelAmount));
        data.setHighlightEnabled(true);
        data.setDrawValues(true);

        mHorizontalBarChart.setData(data);
        mHorizontalBarChart.animateXY(1000, 3000);

        mHorizontalBarChart.setTouchEnabled(false);
        mHorizontalBarChart.setPinchZoom(false);
        mHorizontalBarChart.setDoubleTapToZoomEnabled(false);

        mHorizontalBarChart.setFitBars(true);
        mHorizontalBarChart.setDescription(null);
        mHorizontalBarChart.setNoDataText("No Data to Display");
        mHorizontalBarChart.setNoDataTextColor(Color.WHITE);


        mHorizontalBarChart.setDrawValueAboveBar(false);

       /* mHorizontalBarChart.getAxisLeft().setAxisMaximum(1000); // add dynamically
        mHorizontalBarChart.getAxisLeft().setAxisMinimum(0); // add dynamically

*/
        mHorizontalBarChart.getXAxis().setDrawGridLines(false);
        mHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
        mHorizontalBarChart.getAxisRight().setDrawGridLines(false);

        mHorizontalBarChart.getAxis(YAxis.AxisDependency.LEFT).setDrawGridLines(false);

        mHorizontalBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        XAxis xAxis = mHorizontalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(8);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new MyCustomFormatter());

        YAxis leftAxis = mHorizontalBarChart.getAxis(YAxis.AxisDependency.LEFT);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = mHorizontalBarChart.getAxis(YAxis.AxisDependency.RIGHT);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawLabels(false);

        Legend l = mHorizontalBarChart.getLegend();
        l.setFormSize(10f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(12f);
        l.setTextColor(Color.WHITE);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(5f);
        l.setEnabled(false);

        mHorizontalBarChart.invalidate();
    }

    private BarDataSet getDataSet(String salaryAmount, String purchaseAmount, String employeeAmount, String miscellaneousAmount, String materialAmount, String explosiveAmount, String fuelAmount) {

        int colors[] = {Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.CYAN};

        float salaryFloat = Float.parseFloat(salaryAmount);
        float purchaseFloat = Float.parseFloat(purchaseAmount);
        float employeeFloat = Float.parseFloat(employeeAmount);
        float miscellaneousFloat = Float.parseFloat(miscellaneousAmount);
        float materialFloat = Float.parseFloat(materialAmount);
        float explosiveFloat = Float.parseFloat(explosiveAmount);
        float fuelFloat = Float.parseFloat(fuelAmount);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, salaryFloat));
        entries.add(new BarEntry(1, purchaseFloat));
        entries.add(new BarEntry(2, employeeFloat));
        entries.add(new BarEntry(3, miscellaneousFloat));
        entries.add(new BarEntry(4, materialFloat));
        entries.add(new BarEntry(5, explosiveFloat));
        entries.add(new BarEntry(6, fuelFloat));


        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.expenditure_type));
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        barDataSet.setValueFormatter(new DefaultValueFormatter(1));

        return barDataSet;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList();
        labels.add("Salary Advance");
        labels.add("Purchase Cash");
        labels.add("Employee Salary");
        labels.add("Miscellaneous");
        labels.add("Material Transit");
        labels.add("Explosives");
        labels.add("Fuel ");
        return labels;
    }

    private class MyCustomFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return getXAxisValues().get((int) value);
        }
    }
}

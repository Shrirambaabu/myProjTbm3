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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.freshlancers.sriambalcrusher.R.id.chart;
import static com.freshlancers.sriambalcrusher.utils.Constants.COLLECTION_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.CustomDatePicker.createDialog;
import static com.freshlancers.sriambalcrusher.utils.DateFormat.dayToYear;

/**
 * Created by Ashith VL on 10/20/2017.
 */

public class CollectionsFragment extends Fragment {

    private static final String TAG = "CollectionsFragment";
    private HorizontalBarChart mHorizontalBarChart;
    private TextInputEditText mDateEditFromText, mDateEditToText;
    private TextInputLayout mDateEditFromTextLayout, mDateEditToTextLayout;
    private InputValidation inputValidation;
    private StringRequest request;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);

        Context mCONTEXT = getActivity();
        //  mAmountPieChart = view.findViewById(R.id.chart);
        mHorizontalBarChart = view.findViewById(chart);
        AppCompatButton submit = view.findViewById(R.id.appCompatButtonSubmit);
        mDateEditFromTextLayout = view.findViewById(R.id.textInputLayoutFromDate);
        relativeLayout = view.findViewById(R.id.rel_relative2);
        mDateEditToTextLayout = view.findViewById(R.id.textInputLayoutToDate);
        inputValidation = new InputValidation(mCONTEXT);
        mDateEditFromText = view.findViewById(R.id.textInputEditTextFromDate);
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
        relativeLayout.setVisibility(View.GONE);

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

        request = new StringRequest(Request.Method.POST, COLLECTION_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                handleResponse(s);
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

        Log.e(TAG, "handleResponse: " + result);
        String creditPayAmount, materialTransitAmount, otherAmount, purchaseBalanceAmount;
        try {
            Object json = new JSONTokener(result).nextValue();

            if (json instanceof JSONArray) {

                JSONArray jsonArray = new JSONArray(result);

                JSONObject creditObject = jsonArray.getJSONObject(2);
                creditPayAmount = creditObject.getString("amount");

                JSONObject materialTransitObject = jsonArray.getJSONObject(3);
                materialTransitAmount = materialTransitObject.getString("amount");

                JSONObject otherObject = jsonArray.getJSONObject(1);
                otherAmount = otherObject.getString("amount");

                JSONObject purchaseBalanceObject = jsonArray.getJSONObject(0);
                purchaseBalanceAmount = purchaseBalanceObject.getString("amount");

                if (!creditPayAmount.equals("null") && !materialTransitAmount.equals("null") && !otherAmount.equals("null") && !purchaseBalanceAmount.equals("null")) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    horizontalBarDataSet(creditPayAmount, materialTransitAmount, otherAmount, purchaseBalanceAmount);
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_LONG).show();
                }

            } else if (json instanceof JSONObject) {
                relativeLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No data found", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void horizontalBarDataSet(String creditPayAmount, String materialTransitAmount, String otherAmount, String purchaseBalanceAmount) {

        BarData data = new BarData(getDataSet(creditPayAmount, materialTransitAmount, otherAmount, purchaseBalanceAmount));
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

        mHorizontalBarChart.getXAxis().setDrawGridLines(false);
        mHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
        mHorizontalBarChart.getAxisRight().setDrawGridLines(false);

        mHorizontalBarChart.getAxis(YAxis.AxisDependency.LEFT).setDrawGridLines(false);

        mHorizontalBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        XAxis xAxis = mHorizontalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(4);
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


    private BarDataSet getDataSet(String creditPayAmount, String materialTransitAmount, String otherAmount, String purchaseBalanceAmount) {

        int colors[] = {Color.WHITE, Color.YELLOW, Color.MAGENTA, Color.CYAN};


        float creditFloat = Float.parseFloat(creditPayAmount);
        float materialTransitFloat = Float.parseFloat(materialTransitAmount);
        float otherFloat = Float.parseFloat(otherAmount);
        float purchaseBalanceFloat = Float.parseFloat(purchaseBalanceAmount);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, purchaseBalanceFloat));
        entries.add(new BarEntry(1, otherFloat));
        entries.add(new BarEntry(2, creditFloat));
        entries.add(new BarEntry(3, materialTransitFloat));


        BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.collection_type));
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        barDataSet.setValueFormatter(new DefaultValueFormatter(1));

        return barDataSet;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList();

        labels.add("Purchase Balance");
        labels.add("others ");
        labels.add("Credit Pay");
        labels.add("Material Transit");

        return labels;
    }

    private class MyCustomFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return getXAxisValues().get((int) value);
        }
    }

}

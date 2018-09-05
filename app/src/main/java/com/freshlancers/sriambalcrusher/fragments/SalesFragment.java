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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.interfaces.VolleyCallback;
import com.freshlancers.sriambalcrusher.utils.InputValidation;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_BUSINESS_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_MATERIAL_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.CustomDatePicker.createDialog;
import static com.freshlancers.sriambalcrusher.utils.DateFormat.dayToYear;
import static com.freshlancers.sriambalcrusher.utils.WebService.stringRequest;

/**
 * Created by Ashith VL on 10/20/2017.
 */

public class SalesFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SalesFragment";
    private Context mCONTEXT;
    private PieChart mAmountPieChart;
    private TextInputEditText mDateEditFromText, mDateEditToText;
    private MultiAutoCompleteTextView mMultiBusinessAutoCompleteTextView, mMultiMaterialAutoCompleteTextView;
    private ArrayAdapter<String> businessAdapter, materialAdapter;
    private InputValidation inputValidation;
    private StringRequest request;
    private TextView amount, units, cash, credit;
    private TextInputLayout mDateEditFromTextLayout, mDateEditToTextLayout, businessLayout, materialLayout;
    private LinearLayout linearLayout, linearLayout2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

        mCONTEXT = getActivity();
        inputValidation = new InputValidation(mCONTEXT);

        mAmountPieChart = view.findViewById(R.id.chart);

        CheckBox materialCheckBox = view.findViewById(R.id.material_checkBox);
        CheckBox businessCheckBox = view.findViewById(R.id.business_checkBox);

        linearLayout = view.findViewById(R.id.linear1);
        linearLayout2 = view.findViewById(R.id.linear2);

        linearLayout.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);

        amount = view.findViewById(R.id.amount_rupees);
        units = view.findViewById(R.id.no_units);
        cash = view.findViewById(R.id.cash_rupees);
        credit = view.findViewById(R.id.credit_rupees);

        mDateEditFromText = view.findViewById(R.id.textInputEditTextFromDate);
        mDateEditToText = view.findViewById(R.id.textInputEditTextToDate);
        mDateEditFromTextLayout = view.findViewById(R.id.textInputLayoutFromDate);
        mDateEditToTextLayout = view.findViewById(R.id.textInputLayoutToDate);

        businessLayout = view.findViewById(R.id.textInputLayoutBusiness);
        materialLayout = view.findViewById(R.id.textInputLayoutMaterial);
        mDateEditFromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = createDialog(mDateEditFromText);
                dialog.show(getActivity().getSupportFragmentManager(), TAG);
            }
        });


        mDateEditToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = createDialog(mDateEditToText);
                dialog.show(getActivity().getSupportFragmentManager(), TAG);
            }
        });

        //materialSpinner(view);

        //  settingMultiSelectSpinner(view);

        multiAutoComplete(view);


        materialCheckBox.setOnCheckedChangeListener(this);
        businessCheckBox.setOnCheckedChangeListener(this);

        AppCompatButton submit = view.findViewById(R.id.appCompatButtonSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String fromDate = dayToYear(mDateEditFromText.getText().toString());
                String toDate = dayToYear(mDateEditToText.getText().toString());
                String businessName = mMultiBusinessAutoCompleteTextView.getText().toString();
                String materialName = mMultiMaterialAutoCompleteTextView.getText().toString();

                if (!inputValidation.isInputEditTextFieldValidation(mDateEditFromText, mDateEditFromTextLayout, getString(R.string.error_message_date))) {
                    return;
                }
                if (!inputValidation.isInputEditTextFieldValidation(mDateEditToText, mDateEditToTextLayout, getString(R.string.error_message_date))) {
                    return;
                }

                if (!inputValidation.isMultiTextFieldValidation(mMultiBusinessAutoCompleteTextView, businessLayout, getString(R.string.error_business_name))) {
                    return;
                }

                if (!inputValidation.isMultiTextFieldValidation(mMultiMaterialAutoCompleteTextView, materialLayout, getString(R.string.error_material_name))) {
                    return;
                }

                postRequest(fromDate, toDate, businessName, materialName);

                AppController.getInstance().addToRequestQueue(request, TAG);
            }
        });

        return view;
    }

    private void postRequest(final String fromDate, final String toDate, final String businessName, final String materialName) {

        request = new StringRequest(Request.Method.POST, SALES_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String cashChart, creditChart, totalUnits;
                    if (jsonObject.has("number_of_units") && jsonObject.has("amount") && jsonObject.has("balance")) {

                        cashChart = jsonObject.getString("amount");
                        creditChart = jsonObject.getString("balance");
                        totalUnits = jsonObject.getString("number_of_units");

                        if (totalUnits.equals("null") && cashChart.equals("null") && creditChart.equals("null")) {
                            Toast.makeText(getContext(), "No data found", Toast.LENGTH_LONG).show();
                            linearLayout.setVisibility(View.GONE);
                            linearLayout2.setVisibility(View.GONE);

                        } else {
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayout2.setVisibility(View.VISIBLE);

                            if (totalUnits.equals("null")){
                                totalUnits="0";
                            }if (cashChart.equals("null")){
                                cashChart="0";
                            }if (creditChart.equals("null")){
                                creditChart="0";
                            }

                            units.setText(totalUnits);
                            cash.setText(cashChart);
                            credit.setText(creditChart);
                            int totalAmount = Integer.parseInt(cashChart) + Integer.parseInt(creditChart);

                            amount.setText(String.valueOf(totalAmount));
                            pieDataSet(cashChart, creditChart);
                        }

                    } else {
                        Toast.makeText(getContext(), "No data found", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("business_name", businessName);
                parameters.put("material", materialName);
                parameters.put("from", fromDate);
                parameters.put("to", toDate);

                return parameters;
            }
        };
    }


    private void multiAutoComplete(View view) {

        mMultiBusinessAutoCompleteTextView = view.findViewById(R.id.business_spinner);

        businessAdapter = new ArrayAdapter<String>(mCONTEXT, android.R.layout.simple_dropdown_item_1line);


        stringRequest(SALES_BUSINESS_URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("business_name")) {
                        JSONArray materialJsonArray = jsonObject.getJSONArray("business_name");

                        if (materialJsonArray != null) {
                            for (int i = 0; i < materialJsonArray.length(); i++) {
                                businessAdapter.add(materialJsonArray.getString(i));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        mMultiBusinessAutoCompleteTextView.setAdapter(businessAdapter);
        mMultiBusinessAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mMultiBusinessAutoCompleteTextView.setThreshold(2);

        mMultiMaterialAutoCompleteTextView = view.findViewById(R.id.material_spinner);

        materialAdapter = new ArrayAdapter<String>(mCONTEXT, android.R.layout.simple_dropdown_item_1line);

        stringRequest(SALES_MATERIAL_URL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("material")) {
                        JSONArray materialJsonArray = jsonObject.getJSONArray("material");

                        if (materialJsonArray != null) {
                            for (int i = 0; i < materialJsonArray.length(); i++) {
                                materialAdapter.add(materialJsonArray.getString(i));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mMultiMaterialAutoCompleteTextView.setAdapter(materialAdapter);
        mMultiMaterialAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mMultiMaterialAutoCompleteTextView.setThreshold(2);

    }


    private void pieDataSet(String cashChart, String creditChart) {

        List<PieEntry> entries = new ArrayList<>();
        int colors[] = {Color.WHITE, Color.YELLOW};
        float cashFloat = Float.parseFloat(cashChart);
        float creditFloat = Float.parseFloat(creditChart);

        entries.add(new PieEntry(cashFloat, "Cash"));
        entries.add(new PieEntry(creditFloat, "Credit"));

        PieDataSet set = new PieDataSet(entries, "");
        set.setValueTextSize(14f);
        set.setHighlightEnabled(true);
        set.setSliceSpace(1f);
        set.setColors(colors);

        PieData data = new PieData(set);
        data.setDrawValues(false);

        mAmountPieChart.setData(data);

        // Legends to show on bottom of the graph
        Legend l = mAmountPieChart.getLegend();
        l.setXOffset(140f);
        l.setXEntrySpace(5);
        l.setYEntrySpace(5);

        mAmountPieChart.setDescription(null);
        mAmountPieChart.setDrawEntryLabels(false);

        mAmountPieChart.setUsePercentValues(false);
        mAmountPieChart.setContentDescription("");

        mAmountPieChart.setDrawHoleEnabled(true);
        mAmountPieChart.setTransparentCircleRadius(30f);
        mAmountPieChart.setHoleRadius(20f);
        mAmountPieChart.setHoleColor(Color.TRANSPARENT);

        mAmountPieChart.setNoDataText("No Data to Display");
        mAmountPieChart.setNoDataTextColor(Color.WHITE);

        mAmountPieChart.animateXY(3000, 3000);
        mAmountPieChart.invalidate(); // refresh
    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {

            case R.id.material_checkBox:

                if (isChecked) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < materialAdapter.getCount(); i++) {
                        builder.append(materialAdapter.getItem(i)).append(",");
                        mMultiMaterialAutoCompleteTextView.setText(builder.toString().replaceAll(",$", ""));
                    }
                } else {
                    mMultiMaterialAutoCompleteTextView.setText("");
                }

                break;

            case R.id.business_checkBox:
                if (isChecked) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < businessAdapter.getCount(); i++) {

                        builder.append(businessAdapter.getItem(i)).append(",");
                        mMultiBusinessAutoCompleteTextView.setText(builder.toString().replaceAll(",$", ""));
                    }
                } else {
                    mMultiBusinessAutoCompleteTextView.setText("");
                }

                break;

        }

    }

}


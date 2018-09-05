package com.freshlancers.sriambalcrusher.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.adapters.RecyclerAdapterPurchaseList;
import com.freshlancers.sriambalcrusher.model.PurchaseList;
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

import static com.freshlancers.sriambalcrusher.utils.Constants.PURCHASE_LIST_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.PURCHASE_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.CustomDatePicker.createDialog;
import static com.freshlancers.sriambalcrusher.utils.DateFormat.dayToYear;

public class PurchaseFragment extends Fragment {

    private static final String TAG = "PurchaseFragment";

    private PieChart mAmountPieChart;
    private TextInputEditText mDateEditFromText, mDateEditToText;
    private TextInputLayout mDateEditFromTextLayout, mDateEditToTextLayout;
    private InputValidation inputValidation;
    private Context mCONTEXT;
    private StringRequest request, tableRequest;
    private RelativeLayout relativeLayout, relativeLayout2;
    private TextView amount, balance;
    private RecyclerView recycler_view;
    private RecyclerAdapterPurchaseList recyclerAdapterPurchaseList;
    private ArrayList<PurchaseList> purchaseListArrayList;


    public PurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);

        mCONTEXT = getActivity();
        inputValidation = new InputValidation(mCONTEXT);

        mAmountPieChart = view.findViewById(R.id.chart);

        mDateEditFromTextLayout = view.findViewById(R.id.textInputLayoutFromDate);
        mDateEditToTextLayout = view.findViewById(R.id.textInputLayoutToDate);

        relativeLayout = view.findViewById(R.id.rel_relative2);
        relativeLayout2 = view.findViewById(R.id.rel_relative3);

        recycler_view = view.findViewById(R.id.recycler_view);

        balance = view.findViewById(R.id.credit_rupees);
        amount = view.findViewById(R.id.cash_rupees);

        relativeLayout.setVisibility(View.GONE);
        relativeLayout2.setVisibility(View.GONE);

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


        AppCompatButton submit = view.findViewById(R.id.appCompatButtonSubmit);
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
                postRecyclerView(fromDate, toDate);

                AppController.getInstance().addToRequestQueue(request, TAG);

                recyclerRequest(fromDate, toDate);
                AppController.getInstance().addToRequestQueue(tableRequest, TAG);


            }


        });


        return view;
    }

    private void postRecyclerView(final String fromDate, final String toDate) {
        purchaseListArrayList = new ArrayList<>();
        recyclerAdapterPurchaseList = new RecyclerAdapterPurchaseList(purchaseListArrayList);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mCONTEXT, LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(recyclerAdapterPurchaseList);
        postRequest(fromDate, toDate);

    }

    private void recyclerRequest(final String fromDate, final String toDate) {

        tableRequest = new StringRequest(Request.Method.POST, PURCHASE_LIST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        PurchaseList purchaseList = new PurchaseList();
                        purchaseList.setVendorName(jsonObject.getString("vendor_name"));
                        purchaseList.setPurchaseItem(jsonObject.getString("purchase_item"));

                        purchaseListArrayList.add(purchaseList);
                    }
                    recyclerAdapterPurchaseList.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    private void postRequest(final String fromDate, final String toDate) {

        request = new StringRequest(Request.Method.POST, PURCHASE_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                String cashAmount, creditAmount;

                try {
                    JSONArray jsonArray = new JSONArray(result);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    cashAmount = jsonObject.getString("amount");
                    creditAmount = jsonObject.getString("balance");

                    if (cashAmount.equals("null") && creditAmount.equals("null")) {

                        relativeLayout.setVisibility(View.GONE);
                        relativeLayout2.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_LONG).show();

                    } else {
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout2.setVisibility(View.VISIBLE);
                        if (cashAmount.equals("null")) {
                            cashAmount = "0";
                        }if (creditAmount.equals("null")){
                            creditAmount="0";
                        }
                        amount.setText(cashAmount);
                        balance.setText(creditAmount);
                        pieDataSet(cashAmount, creditAmount);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    private void pieDataSet(String cashAmount, String creditAmount) {

        List<PieEntry> entries = new ArrayList<>();
        int colors[] = {Color.WHITE, Color.YELLOW};

        float cashFloat = Float.parseFloat(cashAmount);
        float creditFloat = Float.parseFloat(creditAmount);

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

}

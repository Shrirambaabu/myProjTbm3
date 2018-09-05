package com.freshlancers.sriambalcrusher.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.freshlancers.sriambalcrusher.utils.InputValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.freshlancers.sriambalcrusher.utils.Constants.COLLECTION_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.EXPLOSIVE_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.CustomDatePicker.createDialog;
import static com.freshlancers.sriambalcrusher.utils.DateFormat.dayToYear;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExplosiveFragment extends Fragment {

    private static final String TAG = "ExplosiveFragment";
    private TextInputEditText mDateEditFromText, mDateEditToText;
    private TextInputLayout mDateEditFromTextLayout, mDateEditToTextLayout;
    private InputValidation inputValidation;
    private Context mCONTEXT;
    private StringRequest request;
    private LinearLayout linearLayoutOne, linearLayoutTwo;
    private TextView amount, totalBoxes, df, ef;

    public ExplosiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explosive, container, false);
        mCONTEXT = getActivity();
        AppCompatButton submit = view.findViewById(R.id.appCompatButtonSubmit);
        mDateEditFromTextLayout = view.findViewById(R.id.textInputLayoutFromDate);
        mDateEditToTextLayout = view.findViewById(R.id.textInputLayoutToDate);
        amount = view.findViewById(R.id.amount_no);
        totalBoxes = view.findViewById(R.id.no_box);
        df = view.findViewById(R.id.df_no_units);
        ef = view.findViewById(R.id.ef_no_units);

        linearLayoutOne = view.findViewById(R.id.linear_one);
        linearLayoutTwo = view.findViewById(R.id.linear_two);
        inputValidation = new InputValidation(mCONTEXT);

        linearLayoutOne.setVisibility(View.GONE);
        linearLayoutTwo.setVisibility(View.GONE);
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

        request = new StringRequest(Request.Method.POST, EXPLOSIVE_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                String totalNoBox, dfNumber, efNumber, totalAmount;

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    totalNoBox = jsonObject.getString("num_of_box");
                    totalAmount = jsonObject.getString("amount");
                    dfNumber = jsonObject.getString("df");
                    efNumber = jsonObject.getString("ef");

                    if (totalNoBox.equals("null") && totalAmount.equals("null") && dfNumber.equals("null") && efNumber.equals("null")) {
                        linearLayoutOne.setVisibility(View.GONE);
                        linearLayoutTwo.setVisibility(View.GONE);
                        totalBoxes.setText("0");
                        amount.setText("0");
                        df.setText("0");
                        ef.setText("0");
                        Toast.makeText(getContext(), "No data Found", Toast.LENGTH_LONG).show();
                    } else {

                        if (totalNoBox.equals("null")){
                            totalNoBox="0";
                        }if (totalAmount.equals("null")){
                            totalAmount="0";
                        }if (dfNumber.equals("null")){
                            dfNumber="0";
                        }if (efNumber.equals("null")){
                            efNumber="0";
                        }

                        totalBoxes.setText(totalNoBox);
                        amount.setText(totalAmount);
                        df.setText(dfNumber);
                        ef.setText(efNumber);

                        linearLayoutOne.setVisibility(View.VISIBLE);
                        linearLayoutTwo.setVisibility(View.VISIBLE);
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

}

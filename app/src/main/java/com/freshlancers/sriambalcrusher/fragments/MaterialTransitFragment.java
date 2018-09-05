package com.freshlancers.sriambalcrusher.fragments;

import android.content.Context;
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
import android.widget.AdapterView;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.interfaces.VolleyCallback;
import com.freshlancers.sriambalcrusher.utils.InputValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.freshlancers.sriambalcrusher.utils.Constants.MATERIAL_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_BUSINESS_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_MATERIAL_URL;
import static com.freshlancers.sriambalcrusher.utils.Constants.SALES_POST_URL;
import static com.freshlancers.sriambalcrusher.utils.CustomDatePicker.createDialog;
import static com.freshlancers.sriambalcrusher.utils.DateFormat.dayToYear;
import static com.freshlancers.sriambalcrusher.utils.WebService.stringRequest;

/**
 * Created by Ashith VL on 10/20/2017.
 */

public class MaterialTransitFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MaterialTransitFragment";
    private Context mCONTEXT;
    private TextInputEditText mDateEditFromText, mDateEditToText;
    private TextInputLayout materialLayout, mDateEditFromTextLayout, mDateEditToTextLayout, businessLayout;
    private CheckBox materialCheckBox, businessCheckBox;
    private MultiAutoCompleteTextView mMultiMaterialAutoCompleteTextView, mMultiBusinessAutoCompleteTextView;
    private ArrayAdapter<String> materialAdapter, businessAdapter;
    private AppCompatButton mAppCompatButton;
    private InputValidation inputValidation;
    private MaterialSpinner spinner_material_selection;
    private StringRequest request;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private String selected = null;
    private TextView materialUnits, materialAmount, materialTrips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material_teansit, container, false);

        mCONTEXT = getActivity();
        inputValidation = new InputValidation(mCONTEXT);

        materialCheckBox = view.findViewById(R.id.material_checkBox);
        businessCheckBox = view.findViewById(R.id.business_checkBox);
        mMultiMaterialAutoCompleteTextView = view.findViewById(R.id.material_autocomplete);
        mMultiBusinessAutoCompleteTextView = view.findViewById(R.id.business_spinner);
        mDateEditFromTextLayout = view.findViewById(R.id.textInputLayoutFromDate);
        mDateEditToTextLayout = view.findViewById(R.id.textInputLayoutToDate);
        mMultiBusinessAutoCompleteTextView.setVisibility(View.GONE);
        businessCheckBox.setVisibility(View.GONE);
        materialLayout = view.findViewById(R.id.textInputLayoutMaterial);
        businessLayout = view.findViewById(R.id.textInputLayoutBusiness);
        mAppCompatButton = view.findViewById(R.id.appCompatButtonSubmit);
        linearLayout = view.findViewById(R.id.linear_layout1);
        relativeLayout = view.findViewById(R.id.rel_relative2);
        materialUnits = view.findViewById(R.id.no_units);
        materialTrips = view.findViewById(R.id.no_units_trips);
        materialAmount = view.findViewById(R.id.amount_rupees);

        linearLayout.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);

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

        spinner_material_selection = (MaterialSpinner) view.findViewById(R.id.material_spinner);
        materialSpinner();

        multiAutoComplete(view);

        checkChange();

        postMethod();

        return view;
    }

    private void checkChange() {
        materialCheckBox.setOnCheckedChangeListener(this);
        businessCheckBox.setOnCheckedChangeListener(this);

    }

    private void postMethod() {

        mAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromDate = dayToYear(mDateEditFromText.getText().toString());
                String toDate = dayToYear(mDateEditToText.getText().toString());
                String transitType = spinner_material_selection.getSelectedItem().toString();
                String materialName = mMultiMaterialAutoCompleteTextView.getText().toString();
                String businessName;

                if (mMultiBusinessAutoCompleteTextView.getVisibility() == View.VISIBLE && businessCheckBox.getVisibility() == View.VISIBLE) {
                    if (!inputValidation.isMultiTextFieldValidation(mMultiBusinessAutoCompleteTextView, businessLayout, getString(R.string.error_business_name))) {
                        return;
                    }
                    businessName = mMultiBusinessAutoCompleteTextView.getText().toString();

                } else {
                    businessName = "nothing";
                }

                if (!inputValidation.isInputEditTextFieldValidation(mDateEditFromText, mDateEditFromTextLayout, getString(R.string.error_message_date))) {
                    return;
                }
                if (!inputValidation.isInputEditTextFieldValidation(mDateEditToText, mDateEditToTextLayout, getString(R.string.error_message_date))) {
                    return;
                }
                if (getString(R.string.tranist_type).equals(selected)) {
                    spinner_material_selection.setError(getString(R.string.error_message));
                    return;
                }
                if (!inputValidation.isMultiTextFieldValidation(mMultiMaterialAutoCompleteTextView, materialLayout, getString(R.string.error_material_name))) {
                    return;
                }

                postRequest(fromDate, toDate, transitType, materialName, businessName);

                AppController.getInstance().addToRequestQueue(request, TAG);

            }
        });

    }

    private void materialSpinner() {
        String[] ITEMS = {"Own", "Customer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mCONTEXT, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_material_selection.setAdapter(adapter);

        spinner_material_selection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected = adapterView.getItemAtPosition(position).toString();

                if (selected.equals("Customer")) {
                    mMultiBusinessAutoCompleteTextView.setVisibility(View.VISIBLE);
                    businessCheckBox.setVisibility(View.VISIBLE);
                } else {
                    mMultiBusinessAutoCompleteTextView.setVisibility(View.GONE);
                    businessCheckBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner_material_selection.setError(getString(R.string.error_message));
            }
        });

    }

    private void multiAutoComplete(View view) {
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

    }

    private void postRequest(final String fromDate, final String toDate, final String transitType, final String materialName, final String businessName) {

        request = new StringRequest(Request.Method.POST, MATERIAL_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                String totalUnits, amount, totalTrips;

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    totalUnits = jsonObject.getString("num_of_unit");
                    amount = jsonObject.getString("amount");
                    totalTrips = jsonObject.getString("num_of_trips");

                    if (totalUnits.equals("null") && amount.equals("null") && totalTrips.equals("null")) {
                        linearLayout.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No data Found", Toast.LENGTH_LONG).show();
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);

                        if (totalUnits.equals("null")) {
                            totalUnits = "0";
                        }
                        if (amount.equals("null")) {
                            amount = "0";
                        }
                        if (totalTrips.equals("null")) {
                            totalTrips = "0";
                        }
                        materialUnits.setText(totalUnits);
                        materialAmount.setText(amount);
                        materialTrips.setText(totalTrips);
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
                parameters.put("from", fromDate);
                parameters.put("to", toDate);
                parameters.put("transit_type", transitType);
                parameters.put("material", materialName);
                parameters.put("business_name", businessName);

                return parameters;
            }
        };
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

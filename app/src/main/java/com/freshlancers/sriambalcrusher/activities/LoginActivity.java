package com.freshlancers.sriambalcrusher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.sqliteHelper.DatabaseHelper;
import com.freshlancers.sriambalcrusher.utils.InputValidation;
import com.freshlancers.sriambalcrusher.utils.Prefs;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextUsername;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        initObjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.activityPaused();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutUsername = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextUsername = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyFromSQLite();
            }
        });
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyFromSQLite() {

        if (!inputValidation.isInputEditTextFieldValidation(textInputEditTextUsername, textInputLayoutUsername, getString(R.string.error_message_username))) {
            return;
        }
        if (!inputValidation.isInputEditTextFieldValidation(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }

        if (databaseHelper.checkUser(textInputEditTextUsername.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {

            Prefs.setAccessTokenInPrefs(textInputEditTextUsername.getText().toString().trim());

            Intent accountsIntent = new Intent(activity, HomeActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextUsername.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();

        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_username_password), Snackbar.LENGTH_LONG).show();
        }

       /* Intent accountsIntent = new Intent(activity, HomeActivity.class);
        accountsIntent.putExtra("EMAIL", textInputEditTextUsername.getText().toString().trim());
        emptyInputEditText();
        startActivity(accountsIntent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);*/

    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextUsername.setText(null);
        textInputEditTextPassword.setText(null);
    }
}

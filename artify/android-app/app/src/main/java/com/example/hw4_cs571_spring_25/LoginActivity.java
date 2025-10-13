package com.example.hw4_cs571_spring_25;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputEditText emailTextInput = findViewById(R.id.emailTextInput);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        TextInputEditText passwordTextInput = findViewById(R.id.passwordTextInput);
        TextView registerClickableTextView = findViewById(R.id.registerClickableTextView);
        TextView loginErrorTextView = findViewById(R.id.loginErrorTextView);
        MaterialButton loginSubmitButton = findViewById(R.id.loginSubmitButton);
        CircularProgressIndicator loginProgressIndicator = findViewById(R.id.loginProgressIndicator);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Login");
        }

        emailTextInput.setOnFocusChangeListener((v, hasFocus) -> {
          if(!hasFocus) {
              String email = emailTextInput.getText().toString().trim();
              if (email.isEmpty()) {
                  emailLayout.setError("Email cannot be empty");
                  emailLayout.setErrorEnabled(true);
              }
              else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                  emailLayout.setError("Invalid email format");
                  emailLayout.setErrorEnabled(true);
              } else {
                  emailLayout.setError(null);
                  emailLayout.setErrorEnabled(false);
              }
          }
        });

        passwordTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = passwordTextInput.getText().toString().trim();
                if (password.isEmpty()) {
                    passwordLayout.setError("Password cannot be empty");
                    emailLayout.setErrorEnabled(true);
                } else {
                    passwordLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                }
            }
        });


        ColorStateList secondaryButtonTint = ContextCompat.getColorStateList(this, R.color.secondary);
        loginSubmitButton.setOnClickListener(v -> {

            loginErrorTextView.setVisibility(View.GONE);
            loginSubmitButton.setEnabled(false);
            loginSubmitButton.setText("");
            loginSubmitButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B2F3F0F0")));
            loginProgressIndicator.setVisibility(View.VISIBLE);

            String email = emailTextInput.getText().toString().trim();
            String password = passwordTextInput.getText().toString().trim();

            LoginRequest loginRequest = new LoginRequest(email, password);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SearchApi searchApi = retrofit.create(SearchApi.class);
            Call<LoginResponse> call = searchApi.login(loginRequest);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        AuthManager.logIn(LoginActivity.this,loginResponse.getToken(),
                                loginResponse.getId(),
                                loginResponse.getEmail(),
                                loginResponse.getFullname(),
                                loginResponse.getProfileImageUrl()
                        );

                        loginSubmitButton.setEnabled(true);
                        loginSubmitButton.setText("Login");
                        loginSubmitButton.setBackgroundTintList(secondaryButtonTint);
                        loginProgressIndicator.setVisibility(View.GONE);

                        Snackbar.make(findViewById(android.R.id.content), "Logged in successfully", Snackbar.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        loginErrorTextView.setVisibility(View.VISIBLE);
                        loginSubmitButton.setEnabled(true);
                        loginSubmitButton.setText("Login");
                        loginSubmitButton.setBackgroundTintList(secondaryButtonTint);
                        loginProgressIndicator.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                    Log.e("LoginAPI", "Error: " + throwable.getMessage());
                    loginErrorTextView.setVisibility(View.VISIBLE);
                    loginSubmitButton.setEnabled(true);
                    loginSubmitButton.setText("Login");
                    loginSubmitButton.setBackgroundTintList(secondaryButtonTint);
                    loginProgressIndicator.setVisibility(View.GONE);
                }
            });
        });


        registerClickableTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
package com.example.hw4_cs571_spring_25;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextInputLayout fullNameLayout = findViewById(R.id.fullNameLayout);
        TextInputEditText fullNameTextInput = findViewById(R.id.fullNameTextInput);
        TextInputLayout fullEmailLayout = findViewById(R.id.fullEmailLayout);
        TextInputEditText fullEmailTextInput = findViewById(R.id.fullEmailTextInput);
        TextInputLayout fullPasswordLayout = findViewById(R.id.fullPasswordLayout);
        TextInputEditText fullPasswordTextInput = findViewById(R.id.fullPasswordTextInput);
        TextView loginClickableTextView = findViewById(R.id.loginClickableTextView);
        TextView registerErrorTextView = findViewById(R.id.registerErrorTextView);
        MaterialButton registerSubmitButton = findViewById(R.id.registerSubmitButton);
        CircularProgressIndicator registerProgressIndicator = findViewById(R.id.registerProgressIndicator);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Register");
        }


        fullNameTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                String name = fullNameTextInput.getText().toString().trim();
                if (name.isEmpty()) {
                    fullNameLayout.setError("Full Name cannot be empty");
                    fullNameLayout.setErrorEnabled(true);
                } else {
                    fullNameLayout.setError(null);
                    fullEmailLayout.setErrorEnabled(false);
                }
            }
        });

        fullEmailTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                String email = fullEmailTextInput.getText().toString().trim();
                if (email.isEmpty()) {
                    fullEmailLayout.setError("Email cannot be empty");
                    fullEmailLayout.setErrorEnabled(true);
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    fullEmailLayout.setError("Invalid email format");
                    fullEmailLayout.setErrorEnabled(true);
                } else {
                    fullEmailLayout.setError(null);
                    fullEmailLayout.setErrorEnabled(false);
                }
            }
        });

        fullPasswordTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = fullPasswordTextInput.getText().toString().trim();
                if (password.isEmpty()) {
                    fullPasswordLayout.setError("Password cannot be empty");
                    fullPasswordLayout.setErrorEnabled(true);
                } else {
                    fullPasswordLayout.setError(null);
                    fullPasswordLayout.setErrorEnabled(false);
                }
            }
        });

        ColorStateList secondaryButtonTint = ContextCompat.getColorStateList(this, R.color.secondary);
        registerSubmitButton.setOnClickListener(v -> {

            registerErrorTextView.setVisibility(View.GONE);
            registerSubmitButton.setEnabled(false);
            registerSubmitButton.setText("");
            registerSubmitButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B2F3F0F0")));
            registerProgressIndicator.setVisibility(View.VISIBLE);

            String name = fullNameTextInput.getText().toString().trim();
            String email = fullEmailTextInput.getText().toString().trim();
            String password = fullPasswordTextInput.getText().toString().trim();

            RegisterRequest registerRequest = new RegisterRequest(name, email, password);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://hw3-cs571-spring-25.uw.r.appspot.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            SearchApi searchApi =retrofit.create(SearchApi.class);
            Call<String> call =searchApi.register(registerRequest);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        String message = response.body();
                        Log.d("RegisterAPI", "Response: " + message);

                        registerSubmitButton.setEnabled(true);
                        registerSubmitButton.setText("Register");
                        registerSubmitButton.setBackgroundTintList(secondaryButtonTint);
                        registerProgressIndicator.setVisibility(View.GONE);

                        Snackbar.make(findViewById(android.R.id.content), "Registered successfully", Snackbar.LENGTH_SHORT).show();

                        LoginRequest loginRequest = new LoginRequest(email, password);
                        Call<LoginResponse> loginCall = searchApi.login(loginRequest);

                        loginCall.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> loginResponse) {
                                if (loginResponse.isSuccessful() && loginResponse.body() != null) {
                                    LoginResponse data = loginResponse.body();

                                    AuthManager.logIn(RegisterActivity.this,
                                            data.getToken(),
                                            data.getId(),
                                            data.getEmail(),
                                            data.getFullname(),
                                            data.getProfileImageUrl());

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("RegisterAPI", "Login failed after registration");

                                }
                            }
                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                Log.d("RegisterAPI", "Login failed after registration");
                            }
                        });

                    } else if (response.code() == 204) {
                        String message = response.body();
                        Log.d("RegisterAPI", "Response: " + message);
                        registerErrorTextView.setVisibility(View.VISIBLE);
                        registerSubmitButton.setEnabled(true);
                        registerSubmitButton.setText("Register");
                        registerSubmitButton.setBackgroundTintList(secondaryButtonTint);
                        registerProgressIndicator.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable throwable) {
                    Log.e("RegisterAPI", "Error: " + throwable.getMessage());
                }
            });
        });

        loginClickableTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });





    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
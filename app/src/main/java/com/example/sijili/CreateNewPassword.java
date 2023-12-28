package com.example.sijili;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sijili.requests.PasswordResetRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateNewPassword extends AppCompatActivity {

    private String BASE_URL = "http://192.168.1.36:4000";
    private Retrofit retrofit;


    private EditText enteredNewPassword;

    RetrofitInterface retrofitInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        enteredNewPassword = findViewById(R.id.enteredNewPassword);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

    }

    public void passToVerifiedSection(View view) {
        String email = getIntent().getStringExtra("emailForPass");
        String newPassword = enteredNewPassword.getText().toString();

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(email, newPassword);

        Call<Void> call = retrofitInterface.resetPassword(passwordResetRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Password reset successful
                    Toast.makeText(CreateNewPassword.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    // Proceed to the next step, e.g., redirect to a new activity
                    Intent intent = new Intent(CreateNewPassword.this, PasswordChanged.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Password reset failed
                    Toast.makeText(CreateNewPassword.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle network errors or other failures
                Toast.makeText(CreateNewPassword.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
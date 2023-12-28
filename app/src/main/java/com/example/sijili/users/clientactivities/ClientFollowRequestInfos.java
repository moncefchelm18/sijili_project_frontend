package com.example.sijili.users.clientactivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sijili.BaseActivity;
import com.example.sijili.R;
import com.example.sijili.RetrofitInterface;
import com.example.sijili.other.NavigationUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.sijili.R; // Replace with your actual package name
import com.example.sijili.requests.BusinessRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientFollowRequestInfos extends BaseActivity {
    private String BASE_URL = "http://192.168.1.36:4000";
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private EditText companyNameTextView;
    private EditText addressTextView;
    private EditText phoneNumberTextView;
    private EditText activityTypeTextView;
    private EditText birthDateTextView;
    private EditText nationalityTextView;
    private EditText numIDTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_follow_request_infos);
        setupNavigationDrawer();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        companyNameTextView = findViewById(R.id.currentCompanyName);
        addressTextView = findViewById(R.id.currentAddress);
        phoneNumberTextView = findViewById(R.id.currentPhoneNumber);
        activityTypeTextView = findViewById(R.id.currentActivityType);
        birthDateTextView = findViewById(R.id.currentBirthDate);
        nationalityTextView = findViewById(R.id.currentNationality);
        numIDTextView = findViewById(R.id.currentNumID);


        // Retrieve the requestId from the intent
        String requestId = getIntent().getStringExtra("requestId");

        // Fetch the details of the selected BusinessRequest using requestId
        fetchBusinessRequestDetails(requestId);
    }

    private void fetchBusinessRequestDetails(String requestId) {
        Toast.makeText(ClientFollowRequestInfos.this, "Waiting", Toast.LENGTH_SHORT).show();

        // Assuming you have a RetrofitInterface method to get details by ID
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String authToken = "Bearer " + preferences.getString("token", "");
        Call<BusinessRequest> call = retrofitInterface.getBusinessRequestById(authToken, requestId);
        call.enqueue(new Callback<BusinessRequest>() {
            @Override
            public void onResponse(Call<BusinessRequest> call, Response<BusinessRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ClientFollowRequestInfos.this, "Succefull", Toast.LENGTH_SHORT).show();

                    // Handle the retrieved BusinessRequest details
                    BusinessRequest businessRequest = response.body();

                    companyNameTextView.setText(businessRequest.getCompanyName());
                    addressTextView.setText(businessRequest.getAddress());
                    phoneNumberTextView.setText(businessRequest.getPhoneNumber());
                    activityTypeTextView.setText(businessRequest.getActivityType());
                    String formattedDate = formatBirthDate(businessRequest.getDateOfBirth());
                    birthDateTextView.setText(formattedDate);
                    nationalityTextView.setText(businessRequest.getNationality());
                    numIDTextView.setText(businessRequest.getNationalityNum());

                    // Handle print btn based on status of payement
                    boolean isPaid = businessRequest.isPaid();
                    String status = businessRequest.getStatus();
                    Button printButton = findViewById(R.id.print_btn);
                    printButton.setEnabled(isPaid);
                    if (isPaid == false && !status.equals("completed")) {
                        // Reset the background color to the default
                        printButton.setBackgroundResource(android.R.drawable.btn_default);
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(ClientFollowRequestInfos.this, "Failed to fetch business request details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BusinessRequest> call, Throwable t) {
                // Handle failure
                Toast.makeText(ClientFollowRequestInfos.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatBirthDate(String rawDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            Date date = inputFormat.parse(rawDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate; // Return the raw date if parsing fails
        }
    }

    public void onUserButtonClick(View view) {
        NavigationUtil.navigateToProfile(this);
    }


    public void paymentProcess(View view) {

        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String authToken = preferences.getString("token", "");
        String authTokenHeader = "Bearer " + authToken;
        String requestId = getIntent().getStringExtra("requestId");


        Call<Void> call = retrofitInterface.validatePayment(authTokenHeader, requestId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Payment validated successfully, update UI or show a message
                    Toast.makeText(ClientFollowRequestInfos.this, "Payment validated successfully", Toast.LENGTH_SHORT).show();
                    // Update your UI or perform any necessary actions
                    openWebPage("https://baridinet.poste.dz/seaal");
                    // For example, you can change the background color of the "Pay" button
                    Button payButton = findViewById(R.id.pay_btn);
                    payButton.setEnabled(false);  // Disable the button
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(ClientFollowRequestInfos.this, "Failed to validate payment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                Toast.makeText(ClientFollowRequestInfos.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteRequest(View view) {
        // Retrieve the requestId from the intent
        String requestId = getIntent().getStringExtra("requestId");

        // Show a confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        performDeleteRequest(requestId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button, do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void printRequest(View view) {
        String requestId = getIntent().getStringExtra("requestId");
        Log.d("RequestId", requestId);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String authToken = "Bearer " + preferences.getString("token", "");
        Log.d("authToken", authToken);
        Call<BusinessRequest> call = retrofitInterface.downloadPDF(authToken, requestId);
        call.enqueue(new Callback<BusinessRequest>() {
            @Override
            public void onResponse(Call<BusinessRequest> call, Response<BusinessRequest> response) {
                Log.d("Test1",""+ response);
                if (response.isSuccessful()) {
                    // Handle the retrieved BusinessRequest details
                    BusinessRequest businessRequest = response.body();
                    Toast.makeText(ClientFollowRequestInfos.this, "Success download PDF", Toast.LENGTH_SHORT).show();

                    // Assuming you have a method to open the PDF using an external PDF viewer
                    openPdfInViewer(businessRequest);
                } else {
                    Log.d("Test2",""+ response.body());
                    Log.d("Test3",""+ response.errorBody());
                    // Handle unsuccessful response
                    Toast.makeText(ClientFollowRequestInfos.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BusinessRequest> call, Throwable t) {
                // Handle failure
                Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ClientFollowRequestInfos.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    used for onClick methodes
    private void performDeleteRequest(String requestId) {
        // Make a Retrofit call to delete the request
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String authToken = "Bearer " + preferences.getString("token", "");

        Call<Void> call = retrofitInterface.deleteCommerceRequest(authToken, requestId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Request deleted successfully, update UI or show a message
                    Toast.makeText(ClientFollowRequestInfos.this, "Request deleted successfully", Toast.LENGTH_SHORT).show();
                    // Perform any necessary actions, such as finishing the activity
                    finish();
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(ClientFollowRequestInfos.this, "Failed to delete request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                Toast.makeText(ClientFollowRequestInfos.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unable to open web browser", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdfInViewer(BusinessRequest businessRequest) {
        String pdfUrl = BASE_URL + "/client/" + businessRequest.getId() + "/register-pdf";
        Uri webpage = Uri.parse(pdfUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unable to open web browser", Toast.LENGTH_SHORT).show();
        }
    }




}

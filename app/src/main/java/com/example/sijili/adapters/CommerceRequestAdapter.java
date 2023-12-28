package com.example.sijili.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sijili.R;
import com.example.sijili.RetrofitInterface;
import com.example.sijili.requests.BusinessRequest;
import com.example.sijili.requests.CommerceRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sijili.RetrofitInterface;
import com.example.sijili.R;
import com.example.sijili.requests.CommerceRequest;
import com.example.sijili.users.serveractivities.ServerManageRequestsActivity;
import com.example.sijili.users.serveractivities.ServerRequestInfos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommerceRequestAdapter extends RecyclerView.Adapter<CommerceRequestAdapter.ViewHolder> {

    private List<CommerceRequest> commerceRequests;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String authToken = "";


    public CommerceRequestAdapter(List<CommerceRequest> commerceRequests) {
        this.commerceRequests = commerceRequests;
    }
    public CommerceRequestAdapter(List<CommerceRequest> commerceRequests, RetrofitInterface retrofitInterface) {
        this.commerceRequests = commerceRequests;
        this.retrofitInterface = retrofitInterface;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commerce_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommerceRequest request = commerceRequests.get(position);


        holder.nameTextView.setText("Name: " + request.getName());
        holder.activityTypeTextView.setText("Activity Type: " + request.getActivityType());
        holder.companyTextView.setText("Company: " + request.getCompanyName());


        holder.rqInfosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, ServerRequestInfos.class);
                intent.putExtra("requestId", request.getId());
                context.startActivity(intent);
            }
        });

        // Set click listeners for accept and refuse buttons
        holder.acceptButton.setOnClickListener(v -> {
            // Handle accept button click
            // You can perform the necessary action here
        });

        holder.refuseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (retrofitInterface != null) {
                    deleteCommerceRequest(request.getUserId());
                } else {
                    Toast.makeText(v.getContext(), "RetrofitInterface is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.rqInfosLayout.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return commerceRequests.size();
    }
    private void deleteCommerceRequest(String requestId) {
        Call<Void> call = retrofitInterface.deleteCommerceRequest("", requestId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful deletion, if needed
                } else {
                    // Handle unsuccessful deletion
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle network errors or other failures
            }
        });
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView activityTypeTextView;
        TextView companyTextView;
        ImageButton acceptButton;
        ImageButton refuseButton;

        LinearLayout rqInfosLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.requestName);
            activityTypeTextView = itemView.findViewById(R.id.requestActivityType);
            companyTextView = itemView.findViewById(R.id.requestCompanyName);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            refuseButton = itemView.findViewById(R.id.refuseButton);
            rqInfosLayout = itemView.findViewById(R.id.layout_infos_rq);
        }
    }
}
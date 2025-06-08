package com.example.prm292_sqlserver;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.prm292_sqlserver.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResult = findViewById(R.id.txtResult);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<User>> call = apiService.getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder result = new StringBuilder();
                    for (User user : response.body()) {
                        result.append(user.getName())
                                .append(" - ")
                                .append(user.getEmail())
                                .append("\n");
                    }
                    txtResult.setText(result.toString());
                } else {
                    txtResult.setText("No data received.");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                txtResult.setText("Error: " + t.getMessage());
            }
        });
    }
}

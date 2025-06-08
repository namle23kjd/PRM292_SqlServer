package com.example.prm292_sqlserver;

import java.util.List;

import com.example.prm292_sqlserver.Model.User;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/users")
    Call<List<User>> getUsers();
}


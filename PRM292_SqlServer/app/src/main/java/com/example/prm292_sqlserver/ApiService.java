package com.example.prm292_sqlserver;

import com.example.prm292_sqlserver.Model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

// First, create this ApiResponse class to match your backend
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }
}

public interface ApiService {

    // ========== USER CRUD OPERATIONS (Updated to use ApiResponse) ==========

    @GET("api/CRUD/users")
    Call<ApiResponse<List<User>>> getUsers();

    @GET("api/CRUD/users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") int id);

    @POST("api/CRUD/users")
    Call<ApiResponse<User>> createUser(@Body User user);

    @PUT("api/CRUD/users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") int id, @Body User user);

    @DELETE("api/CRUD/users/{id}")
    Call<ApiResponse<Object>> deleteUser(@Path("id") int id);

    // ========== ROLE OPERATIONS ==========

    @GET("api/CRUD/roles")
    Call<ApiResponse<List<Object>>> getRoles();

    // ========== SEARCH OPERATIONS ==========

    @GET("api/CRUD/search/users")
    Call<ApiResponse<List<User>>> searchUsers(@Query("name") String name);

    @GET("api/CRUD/search/users")
    Call<ApiResponse<List<User>>> searchUsersByNameAndEmail(@Query("name") String name, @Query("email") String email);

    // ========== UTILITY OPERATIONS ==========

    @GET("api/CRUD/users/by-role/{roleId}")
    Call<ApiResponse<List<User>>> getUsersByRole(@Path("roleId") int roleId);

    @GET("api/CRUD/dashboard/stats")
    Call<ApiResponse<Object>> getDashboardStats();
}
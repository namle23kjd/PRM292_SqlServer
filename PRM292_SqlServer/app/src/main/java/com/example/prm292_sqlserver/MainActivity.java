package com.example.prm292_sqlserver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm292_sqlserver.Model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvResults;
    private ApiService apiService;

    // Search fields
    private EditText etSearchName;

    // Create fields
    private EditText etCreateName, etCreateEmail, etCreatePhone, etCreateAddress,etCreateDob, etCreateRoleId;

    // Update fields
    private EditText etUpdateUserId, etUpdateName, etUpdateEmail, etUpdatePhone, etUpdateAddress, etUpdateDob,etUpdateRoleId;
    private Button btnLoadUserForEdit;
    // Delete fields
    private EditText etDeleteUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // Initialize API service
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Set click listeners
        setClickListeners();

        // Load users on startup
        getAllUsers();
    }

    private void initializeViews() {
        tvResults = findViewById(R.id.tvResults);

        // Search fields
        etSearchName = findViewById(R.id.etSearchName);

        // Create fields
        etCreateName = findViewById(R.id.etCreateName);
        etCreateEmail = findViewById(R.id.etCreateEmail);
        etCreatePhone = findViewById(R.id.etCreatePhone);
        etCreateAddress = findViewById(R.id.etCreateAddress);
        etCreateDob = findViewById(R.id.etCreateDob);
        etCreateRoleId = findViewById(R.id.etCreateRoleId);

        // Update fields
        etUpdateUserId = findViewById(R.id.etUpdateUserId);
        etUpdateName = findViewById(R.id.etUpdateName);
        etUpdateEmail = findViewById(R.id.etUpdateEmail);
        etUpdatePhone = findViewById(R.id.etUpdatePhone);
        etUpdateAddress = findViewById(R.id.etUpdateAddress);
        etUpdateDob = findViewById(R.id.etUpdateDob);
        etUpdateRoleId = findViewById(R.id.etUpdateRoleId);
        btnLoadUserForEdit = findViewById(R.id.btnLoadUserForEdit);
        // Delete fields
        etDeleteUserId = findViewById(R.id.etDeleteUserId);
    }

    private void setClickListeners() {
        Button btnGetUsers = findViewById(R.id.btnGetUsers);
        Button btnAddUser = findViewById(R.id.btnAddUser);
        Button btnEditUser = findViewById(R.id.btnEditUser);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);
        Button btnGetRoles = findViewById(R.id.btnGetRoles);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnGetUsers.setOnClickListener(v -> getAllUsers());
        btnAddUser.setOnClickListener(v -> createUser());
        btnEditUser.setOnClickListener(v -> updateUser());
        btnLoadUserForEdit.setOnClickListener(v -> loadUserForEdit());

        btnDeleteUser.setOnClickListener(v -> deleteUser());
        btnGetRoles.setOnClickListener(v -> getAllRoles());
        btnSearch.setOnClickListener(v -> searchUsers());
    }

    // ========== USER CRUD OPERATIONS ==========

    private void getAllUsers() {
        showLoading("Loading users...");

        Call<ApiResponse<List<User>>> call = apiService.getUsers();
        call.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<User>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        List<User> users = apiResponse.getData();
                        displayUsers(users);
                        showToast("✅ " + apiResponse.getMessage());
                    } else {
                        tvResults.setText("❌ API Error: " + apiResponse.getMessage());
                        showToast("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvResults.setText("❌ Failed to load users\nError code: " + response.code());
                    showToast("Failed to load users");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                String error = "🔗 Connection failed!\n\nError: " + t.getMessage() +
                        "\n\n💡 Check if:\n• Server is running\n• Using correct IP: 192.168.1.15:59125\n• Network connection is active";
                tvResults.setText(error);
                showToast("Connection failed: " + t.getMessage());
            }
        });
    }

    private void createUser() {
        // Validate input fields
        String name = etCreateName.getText().toString().trim();
        String email = etCreateEmail.getText().toString().trim();
        String phone = etCreatePhone.getText().toString().trim();
        String address = etCreateAddress.getText().toString().trim();
        String dob = etCreateDob.getText().toString().trim();
        String roleIdStr = etCreateRoleId.getText().toString().trim();

        if (name.isEmpty()) {
            showToast("❌ Please enter a name");
            etCreateName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showToast("❌ Please enter an email");
            etCreateEmail.requestFocus();
            return;
        }

        if (roleIdStr.isEmpty()) {
            showToast("❌ Please enter a role ID");
            etCreateRoleId.requestFocus();
            return;
        }

        int roleId;
        try {
            roleId = Integer.parseInt(roleIdStr);
        } catch (NumberFormatException e) {
            showToast("❌ Role ID must be a number");
            etCreateRoleId.requestFocus();
            return;
        }

        showLoading("Creating user...");

        // Create user object with input data
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phone.isEmpty() ? null : phone);
        newUser.setAddress(address.isEmpty() ? null : address);
        newUser.setDateOfBirth(dob.isEmpty() ? "1990-01-01T00:00:00" : dob);
        newUser.setRoleId(roleId);

        Call<ApiResponse<User>> call = apiService.createUser(newUser);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        displaySingleUser("USER CREATED", apiResponse.getData());
                        showToast("✅ " + apiResponse.getMessage());
                        clearCreateFields();
                    } else {
                        tvResults.setText("❌ API Error: " + apiResponse.getMessage());
                        showToast("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvResults.setText("❌ Failed to create user\nError code: " + response.code());
                    showToast("Failed to create user");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                tvResults.setText("🔗 Connection failed: " + t.getMessage());
                showToast("Connection failed!");
            }
        });
    }

    private void updateUser() {
        // Validate input fields
        String userIdStr = etUpdateUserId.getText().toString().trim();
        String name = etUpdateName.getText().toString().trim();
        String email = etUpdateEmail.getText().toString().trim();
        String phone = etUpdatePhone.getText().toString().trim();
        String address = etUpdateAddress.getText().toString().trim();
        String dob = etUpdateDob.getText().toString().trim();
        String roleIdStr = etUpdateRoleId.getText().toString().trim();

        if (userIdStr.isEmpty()) {
            showToast("❌ Please enter user ID to update");
            etUpdateUserId.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            showToast("❌ Please enter a name");
            etUpdateName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showToast("❌ Please enter an email");
            etUpdateEmail.requestFocus();
            return;
        }

        if (roleIdStr.isEmpty()) {
            showToast("❌ Please enter a role ID");
            etUpdateRoleId.requestFocus();
            return;
        }

        int userId, roleId;
        try {
            userId = Integer.parseInt(userIdStr);
            roleId = Integer.parseInt(roleIdStr);
        } catch (NumberFormatException e) {
            showToast("❌ User ID and Role ID must be numbers");
            return;
        }

        showLoading("Updating user...");

        // Create user object with input data
        User updateUser = new User();
        updateUser.setName(name);
        updateUser.setEmail(email);
        updateUser.setPhoneNumber(phone.isEmpty() ? null : phone);
        updateUser.setAddress(address.isEmpty() ? null : address);
        updateUser.setDateOfBirth(dob.isEmpty() ? "1990-01-01T00:00:00" : dob);
        updateUser.setRoleId(roleId);

        Call<ApiResponse<User>> call = apiService.updateUser(userId, updateUser);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        displaySingleUser("USER UPDATED (ID: " + userId + ")", apiResponse.getData());
                        showToast("✅ " + apiResponse.getMessage());
                        clearUpdateFields();
                    } else {
                        tvResults.setText("❌ API Error: " + apiResponse.getMessage());
                        showToast("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvResults.setText("❌ Failed to update user\nError code: " + response.code() +
                            "\n\n💡 User ID " + userId + " might not exist.");
                    showToast("Failed to update user");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                tvResults.setText("🔗 Connection failed: " + t.getMessage());
                showToast("Connection failed!");
            }
        });
    }
    private void loadUserForEdit() {
        String userIdStr = etUpdateUserId.getText().toString().trim();

        if (userIdStr.isEmpty()) {
            showToast("❌ Please enter user ID");
            etUpdateUserId.requestFocus();
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            showToast("❌ User ID must be a number");
            return;
        }

        showLoading("🔍 Loading user data...");

        Call<ApiResponse<User>> call = apiService.getUserById(userId);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();

                    etUpdateName.setText(user.getName());
                    etUpdateEmail.setText(user.getEmail());
                    etUpdatePhone.setText(user.getPhoneNumber());
                    etUpdateAddress.setText(user.getAddress());
                    etUpdateDob.setText(user.getDateOfBirth());
                    etUpdateRoleId.setText(String.valueOf(user.getRoleId()));

                    showToast("✅ User loaded. Now you can edit.");
                } else {
                    tvResults.setText("❌ Failed to load user. Check ID or try again.");
                    showToast("⚠️ User not found or API error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                tvResults.setText("🔗 Connection failed: " + t.getMessage());
                showToast("Connection failed.");
            }
        });
    }
    private void deleteUser() {
        String userIdStr = etDeleteUserId.getText().toString().trim();

        if (userIdStr.isEmpty()) {
            showToast("❌ Please enter user ID to delete");
            etDeleteUserId.requestFocus();
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            showToast("❌ User ID must be a number");
            etDeleteUserId.requestFocus();
            return;
        }

        // ✅ Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete user with ID " + userId + "?")
                .setPositiveButton("Yes", (dialog, which) -> performDeleteUser(userId))
                .setNegativeButton("No", null)
                .show();
    }

    private void performDeleteUser(int userId) {
        showLoading("Deleting user...");

        Call<ApiResponse<Object>> call = apiService.deleteUser(userId);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        tvResults.setText("🗑️ USER DELETED\n⏰ " + java.time.LocalTime.now() +
                                "\n═══════════════════════\n\n✅ " + apiResponse.getMessage() +
                                "\nUser ID: " + userId);
                        showToast("✅ " + apiResponse.getMessage());
                        etDeleteUserId.setText("");
                    } else {
                        tvResults.setText("❌ API Error: " + apiResponse.getMessage());
                        showToast("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvResults.setText("❌ Failed to delete user\nError code: " + response.code() +
                            "\n\n💡 User ID " + userId + " might not exist.");
                    showToast("Failed to delete user");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                tvResults.setText("🔗 Connection failed: " + t.getMessage());
                showToast("Connection failed!");
            }
        });
    }

    private void searchUsers() {
        String searchName = etSearchName.getText().toString().trim();

        if (searchName.isEmpty()) {
            showToast("❌ Please enter a name to search");
            etSearchName.requestFocus();
            return;
        }

        showLoading("Searching users...");

        Call<ApiResponse<List<User>>> call = apiService.searchUsers(searchName);
        call.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<User>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        displayUsers("SEARCH RESULTS ('" + searchName + "')", apiResponse.getData());
                        showToast("✅ " + apiResponse.getMessage());
                        etSearchName.setText("");
                    } else {
                        tvResults.setText("❌ API Error: " + apiResponse.getMessage());
                        showToast("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvResults.setText("❌ Search failed\nError code: " + response.code());
                    showToast("Search failed");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                tvResults.setText("🔗 Connection failed: " + t.getMessage());
                showToast("Connection failed!");
            }
        });
    }

    private void getAllRoles() {
        showLoading("Loading roles...");

        Call<ApiResponse<List<Object>>> call = apiService.getRoles();
        call.enqueue(new Callback<ApiResponse<List<Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Object>>> call, Response<ApiResponse<List<Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Object>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        tvResults.setText("🏷️ ALL ROLES\n" +
                                "⏰ " + java.time.LocalTime.now() + "\n" +
                                "═══════════════════════\n\n" +
                                apiResponse.getData().toString());
                        showToast("✅ " + apiResponse.getMessage());
                    } else {
                        tvResults.setText("❌ API Error: " + apiResponse.getMessage());
                        showToast("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvResults.setText("❌ Failed to load roles\nError code: " + response.code());
                    showToast("Failed to load roles");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Object>>> call, Throwable t) {
                tvResults.setText("🔗 Connection failed: " + t.getMessage());
                showToast("Connection failed!");
            }
        });
    }

    // ========== DISPLAY METHODS ==========

    private void displayUsers(List<User> users) {
        displayUsers("ALL USERS", users);
    }

    private void displayUsers(String title, List<User> users) {
        StringBuilder result = new StringBuilder();
        result.append("👥 ").append(title).append("\n");
        result.append("⏰ ").append(java.time.LocalTime.now()).append("\n");
        result.append("═══════════════════════\n\n");

        if (users == null || users.isEmpty()) {
            result.append("📭 No users found.");
        } else {
            for (User user : users) {
                result.append("🆔 ID: ").append(user.getId()).append("\n");
                result.append("👤 Name: ").append(user.getName()).append("\n");
                result.append("📧 Email: ").append(user.getEmail()).append("\n");

                if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                    result.append("📱 Phone: ").append(user.getPhoneNumber()).append("\n");
                }

                if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                    result.append("🏠 Address: ").append(user.getAddress()).append("\n");
                }

                if (user.getDateOfBirth() != null && !user.getDateOfBirth().isEmpty()) {
                    result.append("🎂 DOB: ").append(user.getDateOfBirth()).append("\n");
                }

                result.append("🏷️ Role ID: ").append(user.getRoleId()).append("\n");

                if (user.getRoleName() != null && !user.getRoleName().isEmpty()) {
                    result.append("🏷️ Role: ").append(user.getRoleName()).append("\n");
                }

                result.append("─────────────────────\n\n");
            }
            result.append("📊 Total: ").append(users.size()).append(" users");
        }

        tvResults.setText(result.toString());
    }

    private void displaySingleUser(String title, User user) {
        String result = "👤 " + title + "\n" +
                "⏰ " + java.time.LocalTime.now() + "\n" +
                "═══════════════════════\n\n" +
                "🆔 ID: " + user.getId() + "\n" +
                "👤 Name: " + user.getName() + "\n" +
                "📧 Email: " + user.getEmail() + "\n" +
                "📱 Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A") + "\n" +
                "🏠 Address: " + (user.getAddress() != null ? user.getAddress() : "N/A") + "\n" +
                "🎂 DOB: " + (user.getDateOfBirth() != null ? user.getDateOfBirth() : "N/A") + "\n" +
                "🏷️ Role ID: " + user.getRoleId() + "\n" +
                "🏷️ Role: " + (user.getRoleName() != null ? user.getRoleName() : "N/A");

        tvResults.setText(result);
    }

    // ========== HELPER METHODS ==========

    private void clearCreateFields() {
        etCreateName.setText("");
        etCreateEmail.setText("");
        etCreatePhone.setText("");
        etCreateAddress.setText("");
        etCreateDob.setText("");
        etCreateRoleId.setText("");
    }

    private void clearUpdateFields() {
        etUpdateUserId.setText("");
        etUpdateName.setText("");
        etUpdateEmail.setText("");
        etUpdatePhone.setText("");
        etUpdateAddress.setText("");
        etUpdateDob.setText("");
        etUpdateRoleId.setText("");
    }

    private void showLoading(String message) {
        tvResults.setText("⏳ " + message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
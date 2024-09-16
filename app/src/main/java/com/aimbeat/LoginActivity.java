package com.aimbeat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aimbeat.databinding.ActivityLoginBinding;
import com.aimbeat.viewmodels.AuthViewModel;

public class LoginActivity extends ComponentActivity {

    private AuthViewModel authViewModel;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe authentication state
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // User is logged in, navigate to Task Management screen
                navigateToTaskManagement();
            }
        });

        // Observe authentication errors
        authViewModel.getAuthError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (validateInputs(email, password)) {
            authViewModel.signIn(email, password);
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void navigateToTaskManagement() {
        Intent intent = new Intent(LoginActivity.this, TaskManagementActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}

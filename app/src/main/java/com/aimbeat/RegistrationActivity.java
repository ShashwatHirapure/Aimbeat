package com.aimbeat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aimbeat.databinding.ActivityRegistrationBinding;
import com.aimbeat.viewmodels.AuthViewModel;

public class RegistrationActivity extends ComponentActivity {

    private AuthViewModel authViewModel;
    ActivityRegistrationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe authentication errors
        authViewModel.getAuthError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // User is logged in, navigate to Task Management screen
                navigateToLogin();
            }
        });
        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.loginTextView.setOnClickListener(v -> navigateToLogin());
    }

    private void registerUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (validateInputs(email, password)) {
            authViewModel.register(email, password);
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

    private void navigateToLogin() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the registration activity
    }
}

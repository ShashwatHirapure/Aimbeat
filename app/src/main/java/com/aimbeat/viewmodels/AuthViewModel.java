package com.aimbeat.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private final FirebaseAuth mAuth;
    private final MutableLiveData<FirebaseUser> currentUser;
    private final MutableLiveData<String> authError;

    public AuthViewModel() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = new MutableLiveData<>(mAuth.getCurrentUser());
        authError = new MutableLiveData<>();
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.setValue(mAuth.getCurrentUser());
                    } else {
                        // Set the error message
                        authError.setValue(task.getException() != null ? task.getException().getMessage() : "Sign-in failed");
                    }
                });
    }

    public void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.setValue(mAuth.getCurrentUser());
                    } else {
                        // Set the error message
                        authError.setValue(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        currentUser.setValue(null);
    }
}

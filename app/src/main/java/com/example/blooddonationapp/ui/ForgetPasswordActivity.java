package com.example.blooddonationapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ActivityForgetPasswordBinding binding;
    private Button Btn;
    private FirebaseAuth authProfile;
    private String resetEmail;
    private SwipeRefreshLayout swipeContainer;

    private static final String TAG = "ForgetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Forget Password");
        swipeToRefresh();

        Btn = findViewById(R.id.resetBtn);
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEmail = binding.inputResetEmail.getText().toString();
                if(resetEmail.isEmpty())
                {
                    binding.inputResetEmail.setError("Type your email");
                    binding.inputResetEmail.requestFocus();
                }
                else
                {
                    resetPassword();
                }
            }
        });
    }
    private void swipeToRefresh()
    {
        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light,android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    private void resetPassword()
    {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgetPasswordActivity.this, "Please check your inbox for password reset link.", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    try {
                        throw task.getException();
                    }
                    catch(FirebaseAuthInvalidUserException e)
                    {
                        binding.inputResetEmail.setError("User does not exists or is no longer valid. Please register again.");
                        binding.inputResetEmail.requestFocus();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
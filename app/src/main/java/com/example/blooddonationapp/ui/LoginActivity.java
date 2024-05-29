package com.example.blooddonationapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private String email,password;
    private ProgressBar progressBar2;
    private FirebaseAuth authProfile;
    private Button button10;
    private static final String TAG = "LoginActivity";
    private SwipeRefreshLayout swipeContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        swipeToRefresh();

        email=binding.inputEmail1.getText().toString();
        password=binding.inputPassword1.getText().toString();
        progressBar2=findViewById(R.id.progressbarID2);
        authProfile = FirebaseAuth.getInstance();

        button10=findViewById(R.id.signInBtn);
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=binding.inputEmail1.getText().toString();
                password=binding.inputPassword1.getText().toString();
                if(email.isEmpty())
                {
                    binding.inputEmail1.setError("Type your email");
                    binding.inputEmail1.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    binding.inputEmail1.setError("Enter valid Email Address");
                    binding.inputEmail1.requestFocus();
                }
                else if(password.isEmpty())
                {
                    binding.inputPassword1.setError("Type your password");
                    binding.inputPassword1.requestFocus();
                }
                else
                {
                    progressBar2.setVisibility(View.VISIBLE);
                    loginUser(email,password);
                }
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
        binding.forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
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

    private void loginUser(String email,String password)
    {
        authProfile.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                        Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        progressBar2.setVisibility(View.GONE);
                }
                else
                {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }
                    catch(FirebaseAuthInvalidUserException e)
                    {
                        binding.inputEmail1.setError("User does not exists or is no longer valid. Please register again.");
                        binding.inputEmail1.requestFocus();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                        binding.inputEmail1.setError("Invalid credentials. Kindly, check and re-enter.");
                        binding.inputEmail1.requestFocus();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(LoginActivity.this, "SomeThing went wrong!!!", Toast.LENGTH_SHORT).show();
                }
                progressBar2.setVisibility(View.GONE);
            }
        });
    }

    /*private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    protected void onStart()
    {
        super.onStart();
        if(authProfile.getCurrentUser()!=null)
        {
            Toast.makeText(LoginActivity.this, "Already Logged In!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(LoginActivity.this, "You can login now", Toast.LENGTH_SHORT).show();
        }
    }

}
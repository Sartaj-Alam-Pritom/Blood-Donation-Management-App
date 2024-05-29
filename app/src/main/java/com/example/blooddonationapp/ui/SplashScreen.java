package com.example.blooddonationapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        progressBar = (ProgressBar) findViewById(R.id.progressbarID);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //if(FirebaseAuth.getInstance().getCurrentUser()==null)
               // {
                    doWork();
                    startApp();
               // }
            }
        });
        thread.start();
    }
    public void doWork(){
        for(progress=34;progress<=100;progress=progress+33)
        {
            try{
                Thread.sleep(500);
                progressBar.setProgress(progress);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void startApp(){
        Intent intent= new Intent(SplashScreen.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
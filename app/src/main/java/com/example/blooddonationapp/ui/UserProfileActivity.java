package com.example.blooddonationapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    public static final int DISPLAY_SHOW_TITLE = android.app.ActionBar.DISPLAY_SHOW_TITLE;
    private TextView textname,textemail,textdob,textgender,textblood,textphone,textaddress;
    private String Name,Email,Dob,Gender,Blood,Phone,Address,image;
    private ImageView imageView;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private Button accessButton,resetButton;
    private boolean access_granted =true,dataLocked=false;
    private long access_until;

    private DatabaseReference databaseReference;
    private long unlockTimeMillis;
    private CountdownService countdownService;
    private TextView countdownTextView;
    private CountDownTimer countDownTimer;
    private long countdownDuration = 3 * 30 * 24 * 60 * 60 * 1000L;
    private boolean DataLocked ;
    private DatabaseReference userReference;
    private SwipeRefreshLayout swipeContainer;
    private boolean isCountdownActive = false ;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        FirebaseApp.initializeApp(this);

        getSupportActionBar().setTitle("My Account");

        swipeToRefresh();

        textname = findViewById(R.id.textview_name);
        textemail = findViewById(R.id.textview_email);
        textdob = findViewById(R.id.textview_dob);
        textgender = findViewById(R.id.textview_gender);
        textblood = findViewById(R.id.textview_blood);
        textphone = findViewById(R.id.textview_phone);
        textaddress = findViewById(R.id.textview_address);
        progressBar = findViewById(R.id.progressbarID);
        accessButton = findViewById(R.id.accessButton);
        resetButton = findViewById(R.id.resetButton);
        countdownTextView = findViewById(R.id.countdownTextView);

        imageView = findViewById(R.id.propic);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });

        sharedPreferences = getSharedPreferences("CountdownPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("is_countdown_active", false)) {
            countdownDuration = sharedPreferences.getLong("countdown_duration", 0);
            startCountdown();
        }

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    DataLocked = readUserDetails.dataLocked;
                    if (DataLocked) {
                        accessButton.setEnabled(false);
                        accessButton.setText("Data Locked");
                        resetButton.setVisibility(View.VISIBLE);
                       // resetButton.setEnabled(true);
                        resetButton.setBackgroundTintList(ContextCompat.getColorStateList(UserProfileActivity.this,R.color.dark_green));
                    }
                    else
                    {
                        resetButton.setVisibility(View.GONE);
                        referenceProfile.child("access_until").setValue(0);
                        updateUI();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not available at this moment.", Toast.LENGTH_SHORT).show();
            }
        });
        
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userReference = databaseReference.child("Registered User").child(userId);
        }

        countdownService = new CountdownService();

        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DataLocked) {
                    accessButton.setEnabled(false);
                    accessButton.setText("Data Locked");
                    lockData();
                    resetButton.setVisibility(View.VISIBLE);
                    //resetButton.setEnabled(true);
                    resetButton.setBackgroundTintList(ContextCompat.getColorStateList(UserProfileActivity.this,R.color.dark_green));
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                countdownDuration = 0;
                countdownTextView.setText("");
                DataLocked = false;
                String userID = firebaseUser.getUid();
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User").child(userID);
                referenceProfile.child("dataLocked").setValue(DataLocked=false, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            // Data updated successfully
                            Toast.makeText(UserProfileActivity.this, "Data is not locked!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the error
                            Toast.makeText(UserProfileActivity.this, "Failed to open data " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                updateUI();
                accessButton.setEnabled(true);
                accessButton.setText("I HAVE GIVEN BLOOD");
                resetButton.setVisibility(View.GONE);
            }
        });


        if (savedInstanceState != null) {
            DataLocked = savedInstanceState.getBoolean("dataLocked", false);
            if (DataLocked) {
                accessButton.setEnabled(false);
                accessButton.setText("Data Locked");
                startCountdown();
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("countdown_update");
        intentFilter.addAction("countdown_finished");
        registerReceiver(countdownReceiver, intentFilter);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not available at this moment.", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
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

    private BroadcastReceiver countdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("countdown_update")) {
                    long timeLeft = intent.getLongExtra("countdown_time_left", 0);
                    updateCountdownText(timeLeft);
                } else if (intent.getAction().equals("countdown_finished")) {
                    DataLocked = false;
                    String userID = firebaseUser.getUid();
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User").child(userID);
                    referenceProfile.child("dataLocked").setValue(DataLocked=false, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                Toast.makeText(UserProfileActivity.this, "Data is not locked!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserProfileActivity.this, "Failed to open data " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    updateUI();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("countdown_finished");
        registerReceiver(countdownReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(countdownReceiver);
    }


    private void lockData() {
        if (userReference != null) {
            DataLocked = true;

            String userID = firebaseUser.getUid();
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User").child(userID);
            referenceProfile.child("dataLocked").setValue(DataLocked=true, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(UserProfileActivity.this, "Data is locked!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to lock data " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            long unlockTimestamp = System.currentTimeMillis() + countdownDuration;
            referenceProfile.child("access_until").setValue(unlockTimestamp);
            Intent countdownIntent = new Intent(this, CountdownService.class);
            countdownIntent.putExtra("countdown_duration", countdownDuration);
            startService(countdownIntent);
            updateUI();
        } else {
            Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountdown() {
        isCountdownActive = true; // Countdown is active
        countDownTimer = new CountDownTimer(countdownDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountdownText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isCountdownActive = false;
                DataLocked = false;
                String userID = firebaseUser.getUid();
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User").child(userID);
                referenceProfile.child("dataLocked").setValue(DataLocked=false, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            Toast.makeText(UserProfileActivity.this, "Data is not locked!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Failed to open data " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                accessButton.setEnabled(true);
                accessButton.setText("I HAVE GIVEN BLOOD");
                resetButton.setVisibility(View.GONE);
                referenceProfile.child("access_until").setValue(0);
                updateUI();
            }
        };
        countDownTimer.start();
    }

    private void updateUI() {

        if (DataLocked) {

            countdownTextView.setVisibility(View.VISIBLE);

            countdownService.setOnTickListener(new CountdownService.OnTickListener() {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateCountdownText(millisUntilFinished);
                }
                @Override
                public void onFinish() {

                }
            });
        } else {
            countdownTextView.setVisibility(View.GONE);
        }
    }

    private void updateCountdownText(long millisUntilFinished) {
        long days = millisUntilFinished / (1000 * 60 * 60 * 24);
        long hours = (millisUntilFinished % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millisUntilFinished % (1000 * 60)) / 1000;
        String countdownText = String.format("   Unlock in: %02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        countdownTextView.setText(countdownText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Intent stopServiceIntent = new Intent(this, CountdownService.class);
        stopService(stopServiceIntent);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("countdown_duration", countdownDuration);
        editor.putBoolean("is_countdown_active", isCountdownActive);
        editor.apply();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        DatabaseReference referenceProfile2 = FirebaseDatabase.getInstance().getReference("Registered User2");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null)
                {
                    Name = readUserDetails.name;
                    Email = readUserDetails.email;
                    Dob = readUserDetails.date_of_Birth;
                    Gender = readUserDetails.gender;
                    Blood = readUserDetails.blood_Group;
                    Phone = readUserDetails.phone;
                    Address = readUserDetails.address;

                    textname.setText(Name);
                    textemail.setText(Email);
                    textdob.setText(Dob);
                    textgender.setText(Gender);
                    textblood.setText(Blood);
                    textphone.setText(Phone);
                    textaddress.setText(Address);

                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(imageView);
                    if (uri != null) {
                        image = uri.toString();
                        FirebaseDatabase.getInstance().getReference("Registered User")
                                .child(firebaseUser.getUid())
                                .child("image")
                                .setValue(image);
                    }
                }
                else
                {
                    accessButton.setEnabled(false);
                    referenceProfile2.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                            if(readUserDetails!=null)
                            {
                                Name = readUserDetails.name;
                                Email = readUserDetails.email;
                                Dob = readUserDetails.date_of_Birth;
                                Gender = readUserDetails.gender;
                                Blood = readUserDetails.blood_Group;
                                Phone = readUserDetails.phone;
                                Address = readUserDetails.address;

                                textname.setText(Name);
                                textemail.setText(Email);
                                textdob.setText(Dob);
                                textgender.setText(Gender);
                                textblood.setText(Blood);
                                textphone.setText(Phone);
                                textaddress.setText(Address);

                                Uri uri = firebaseUser.getPhotoUrl();
                                Picasso.get().load(uri).into(imageView);
                            }
                            else
                            {
                                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not available at this moment.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id=item.getItemId();
        if(id == R.id.menu_list)
        {
            Intent intent =new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            //startActivity(getIntent());
            finish();
            //overridePendingTransition(0,0);
        }
        else if (id == R.id.menu_my_account)
        {
            Intent intent =new Intent(UserProfileActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_profile)
        {
            Intent intent =new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_email)
        {
            Intent intent =new Intent(UserProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_settings)
        {
            Toast.makeText(UserProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_change_password)
        {
            Intent intent =new Intent(UserProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_delete_profile)
        {
            Intent intent =new Intent(UserProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_logout)
        {
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(UserProfileActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.blooddonationapp.adapter.MainAdapter;
import com.example.blooddonationapp.databinding.ActivityMainBinding;
import com.example.blooddonationapp.ui.ChangePasswordActivity;
import com.example.blooddonationapp.ui.DeleteProfileActivity;
import com.example.blooddonationapp.ui.DetailActivity;
import com.example.blooddonationapp.ui.LoginActivity;
import com.example.blooddonationapp.ui.ReadWriteUserDetails;
import com.example.blooddonationapp.ui.UpdateEmailActivity;
import com.example.blooddonationapp.ui.UpdateProfileActivity;
import com.example.blooddonationapp.ui.UserProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    RecyclerView recyclerView;
    private ActivityMainBinding binding;
    private List<ReadWriteUserDetails> datalist;
    private MainAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("List of the Users");
        authProfile = FirebaseAuth.getInstance();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshLayout.setRefreshing((false));
            }
        });

       datalist = new ArrayList<>();
        adapter = new MainAdapter(MainActivity.this,datalist);
        binding.recyclerViewId.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datalist.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataSnapshot accessUntilSnapshot = itemSnapshot.child("access_until");

                    if (accessUntilSnapshot.exists() && accessUntilSnapshot.getValue() instanceof Long) {
                        Long accessUntilTimestamp = accessUntilSnapshot.getValue(Long.class);

                        ReadWriteUserDetails writeUserDetails = itemSnapshot.getValue(ReadWriteUserDetails.class);

                        if (writeUserDetails != null) {
                            writeUserDetails.setaccess_until(accessUntilTimestamp);

                            if (writeUserDetails != null && !writeUserDetails.isDataLocked()) {
                                datalist.add(writeUserDetails);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

       binding.search.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               searchList(s.toString());
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

    }

    private void searchList(String text)
    {
        ArrayList<ReadWriteUserDetails> searchList = new ArrayList<>();
        String[] keywords = text.toLowerCase().split("\\s+");

        for(ReadWriteUserDetails writeUserDetails : datalist )
        {
            String bloodGroup = writeUserDetails.getBlood_Group().toLowerCase();
            String address = writeUserDetails.getAddress().toLowerCase();
            String name = writeUserDetails.getName().toLowerCase();
            String phone = writeUserDetails.getPhone().toLowerCase();
            boolean match = false;

            for (String keyword : keywords) {
                if (name.contains(keyword) || phone.contains(keyword) ||
                        bloodGroup.contains(keyword) || address.contains(keyword)) {
                    match = true;
                    break;
                }
            }

            if (match) {
                searchList.add(writeUserDetails);
            }
        }

        if (adapter != null) {
            adapter.searchDataList(searchList);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

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
            Intent intent =new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_my_account)
        {
            Intent intent =new Intent(MainActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_profile)
        {
            Intent intent =new Intent(MainActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_email)
        {
            Intent intent =new Intent(MainActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_settings)
        {
            Toast.makeText(MainActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_change_password)
        {
            Intent intent =new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_delete_profile)
        {
            Intent intent =new Intent(MainActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_logout)
        {
            authProfile.signOut();
            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
package com.example.blooddonationapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView name,blood,phone,address;
    String call;
    ImageView imageView;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private SwipeRefreshLayout swipeContainer;

    static int PERMISSION_CODE = 100;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        name = findViewById(R.id.textview_name);
        blood = findViewById(R.id.textview_blood);
        phone = findViewById(R.id.textview_phone);
        address = findViewById(R.id.textview_address);
        imageView = findViewById(R.id.propic);
        button = findViewById(R.id.callBtn);

        button.setBackgroundTintList(ContextCompat.getColorStateList(DetailActivity.this,R.color.dark_green));


        authProfile = FirebaseAuth.getInstance();

        swipeToRefresh();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            String imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(imageView);
            //Glide.with(this).load(bundle.getString("Image")).into(imageView);
            name.setText(bundle.getString("Name"));
            blood.setText(bundle.getString("Blood_Group"));
            phone.setText(bundle.getString("Phone"));
            address.setText(bundle.getString("Address"));

            call = phone.getText().toString();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+call));
                    startActivity(intent);
                }
            });
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
            Intent intent =new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            //startActivity(getIntent());
            finish();
            //overridePendingTransition(0,0);
        }
        else if (id == R.id.menu_my_account)
        {
            Intent intent =new Intent(DetailActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_profile)
        {
            Intent intent =new Intent(DetailActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_email)
        {
            Intent intent =new Intent(DetailActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_settings)
        {
            Toast.makeText(DetailActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_change_password)
        {
            Intent intent =new Intent(DetailActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_delete_profile)
        {
            Intent intent =new Intent(DetailActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_logout)
        {
            authProfile.signOut();
            Toast.makeText(DetailActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(DetailActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(DetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
package com.example.blooddonationapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonaterBinding;
import com.example.blooddonationapp.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;
    private String name,phone,email,password,blood,DoB,address,pass2,selectedDivision,selectedzilla,selectedupzilla,gender,image;
    private RadioGroup radioGroupGender;
    private RadioButton radioButton;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private Spinner zillaSpinner,upzillaSpinner,divisionSpinner,bloodSpinner;
    private ArrayAdapter<CharSequence> zillaAdapter,upzillaAdapter,divisionAdapter,bloodAdapter;

    private DatePickerDialog picker;
    private boolean access_granted =true,dataLocked=false;
    private long access_until;
    private Button button1,button2,button3;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Update Profile");
        swipeToRefresh();
        divisionSpinner=findViewById(R.id.division);
        bloodSpinner=findViewById(R.id.bloodGroupsSpinner);
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupGender.clearCheck();
        authProfile =FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);
        button1=findViewById(R.id.button_upload_profile_pic);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfileActivity.this,UploadProfilePicActivity.class));
                finish();
            }
        });

        divisionSpinner=findViewById(R.id.division);
        bloodSpinner=findViewById(R.id.bloodGroupsSpinner);


        divisionAdapter = ArrayAdapter.createFromResource(this,R.array.array_BD_division,R.layout.spinner_layout);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(divisionAdapter);
        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zillaSpinner=findViewById(R.id.zilla);
                selectedDivision = divisionSpinner.getSelectedItem().toString();
                int parentID = parent.getId();
                if(parentID==R.id.division)
                {
                    switch(selectedDivision)
                    {
                        case "Select Your Division":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_default_division,R.layout.spinner_layout);
                            break;
                        case "Rajshahi":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Rajshahi_division,R.layout.spinner_layout);
                            break;
                        case "Dhaka":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Dhaka_division,R.layout.spinner_layout);
                            break;
                        case "Rangpur":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Rangpur_division,R.layout.spinner_layout);
                            break;
                        case "Chattagram":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Chattagram_division,R.layout.spinner_layout);
                            break;
                        case "Khulna":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Khulna_division,R.layout.spinner_layout);
                            break;
                        case "Barishal":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Barishal_division,R.layout.spinner_layout);
                            break;
                        case "Sylhet":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Sylhet_division,R.layout.spinner_layout);
                            break;
                        case "Mymensingh":zillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Mymensingh_division,R.layout.spinner_layout);
                            break;
                        default: break;
                    }
                    zillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    zillaSpinner.setAdapter(zillaAdapter);
                    zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            upzillaSpinner=findViewById(R.id.upzilla);
                            selectedzilla = zillaSpinner.getSelectedItem().toString();
                            int parentID2 = parent.getId();
                            if(parentID2==R.id.zilla)
                            {
                                switch(selectedzilla)
                                {
                                    case "Select Your Zilla":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_default_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Rajshahi":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Rajshahi_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Naogaon":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Naogaon_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Bogura":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Bogura_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Chapainawabganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Chapainawabganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Natore":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Natore_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Pabna":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Pabna_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Sirajganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Sirajganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Joypurhat":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Joypurhat_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Dhaka":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Dhaka_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Kishoreganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Kishoreganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Gazipur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Gazipur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Gopalganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Gopalganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Tangail":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Tangail_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Narayanganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Narayanganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Faridpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Faridpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Madaripur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Madaripur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Manikganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Manikganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Munshiganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Munshiganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Rajbari":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Rajbari_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Shariatpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Shariatpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Narsingdi":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Narsingdi_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Rangpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Rangpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Gaibandha":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Gaibandha_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Nilphamari":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Nilphamari_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Kurigram":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Kurigram_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Lalmonirhat":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Lalmonirhat_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Dinajpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Dinajpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Thakurgaon":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Thakurgaon_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Panchagarh":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Panchagarh_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Chattagram":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Chattagram_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Coxs Bazar":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Coxs_Bazar_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Rangamati":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Rangamati_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Bandarban":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Bandarban_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Khagrachhari":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Khagrachhari_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Feni":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Feni_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Lakshmipur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Lakshmipur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Cumilla":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Cumilla_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Noakhali":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Noakhali_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Brahmanbaria":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Brahmanbaria_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Chandpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Chandpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Khulna":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Khulna_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Bagherhat":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Bagherhat_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Sathkhira":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Sathkhira_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Jashore":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Jashore_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Magura":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Magura_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Jhenaidah":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Jhenaidah_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Narail":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Narail_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Kushtia":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Kushtia_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Chuadanga":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Chuadanga_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Meherpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Meherpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Barishal":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Barishal_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Barguna":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Barguna_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Bhola":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Bhola_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Jhalakathi":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Jhalakathi_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Pirojpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Pirojpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Patuakhali":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Patuakhali_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Sylhet":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Sylhet_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Habiganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Habiganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Moulvibazar":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Moulvibazar_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Sunamganj":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Sunamganj_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Mymensingh":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Mymensingh_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Jamalpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Jamalpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Netrokona":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Netrokona_Zilla,R.layout.spinner_layout);
                                        break;
                                    case "Sherpur":upzillaAdapter=ArrayAdapter.createFromResource(parent.getContext(),R.array.array_Sherpur_Zilla,R.layout.spinner_layout);
                                        break;
                                    default: break;
                                }
                                upzillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                upzillaSpinner.setAdapter(upzillaAdapter);
                                upzillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        selectedupzilla=upzillaSpinner.getSelectedItem().toString();
                                        address = selectedupzilla+" , "+selectedzilla+" district , "+selectedDivision;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bloodAdapter = ArrayAdapter.createFromResource(this,R.array.array_blood,R.layout.spinner_layout);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(bloodAdapter);
        bloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                blood=bloodSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSADoB[] = DoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1])-1;
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.inputDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year,month,day);
                picker.show();
            }
        });

        progressBar = findViewById(R.id.progressbarID1);
        button3=findViewById(R.id.button_update_profile);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                updateProfile(firebaseUser);
            }
        });

        button2=findViewById(R.id.button_profile_update_email);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser)
    {
        int selectedGenderID = radioGroupGender.getCheckedRadioButtonId();
        radioButton = findViewById(selectedGenderID);
        name = binding.inputName.getText().toString();
        phone = binding.inputNumber.getText().toString();
        DoB = binding.inputDate.getText().toString();
        if(name.isEmpty())
        {
            binding.inputName.setError("Type your name");
            binding.inputName.requestFocus();
        }
        else if(radioGroupGender.getCheckedRadioButtonId()==-1)
        {
            binding.textviewGender.setError("Select Your Gender");
            binding.textviewGender.requestFocus();
        }
        else if(DoB.isEmpty())
        {
            binding.inputDate.setError("Type your date of birth (dd/mm/yyyy)");
            binding.inputDate.requestFocus();
        }
        else if(blood.equals("Select Your Blood Group"))
        {
            binding.Blood.setError("Select Your Blood Group");
            binding.Blood.requestFocus();
        }
        else if(selectedDivision.equals("Select Your Division"))
        {
            binding.Add.setError("Select Your Divison");
            binding.Add.requestFocus();
        }
        else if(selectedzilla.equals("Select Your Zilla"))
        {
            binding.Add.setError("Select Your Zilla");
            binding.Add.requestFocus();
        }
        else if(selectedupzilla.equals("Select Your Upzilla"))
        {
            binding.Add.setError("Select Your Upzilla");
            binding.Add.requestFocus();
        }
        else if(phone.isEmpty())
        {
            binding.inputNumber.setError("Type your phone number");
            binding.inputNumber.requestFocus();
        }
        else if(phone.length() != 11)
        {
            binding.inputNumber.setError("Re-type your phone number in 11 digits");
            binding.inputNumber.requestFocus();
        }
        else
        {
            gender=radioButton.getText().toString();
            name = binding.inputName.getText().toString();
            phone = binding.inputNumber.getText().toString();
            DoB = binding.inputDate.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(name, email, phone, blood, password, gender, DoB, address,access_granted,access_until,dataLocked,image);
            DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Registered User");
            DatabaseReference referenceProfile2 =FirebaseDatabase.getInstance().getReference("Registered User2");

            String userID = firebaseUser.getUid();
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        referenceProfile2.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    firebaseUser.updateProfile(profileUpdates);
                                    Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    try {
                                        throw task.getException();
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
        progressBar.setVisibility(View.GONE);
    }

    private void showProfile(FirebaseUser firebaseUser) {

        String userIDofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        DatabaseReference referenceProfile2 = FirebaseDatabase.getInstance().getReference("Registered User2");
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null)
                {
                    name = readUserDetails.name;
                    DoB = readUserDetails.date_of_Birth;
                    gender = readUserDetails.gender;
                    blood = readUserDetails.blood_Group;
                    address = readUserDetails.address;
                    phone = readUserDetails.phone;

                    binding.inputName.setText(name);
                    binding.inputDate.setText(DoB);
                    binding.inputNumber.setText(phone);

                    if(gender.equals("Male"))
                    {
                        radioButton =findViewById(R.id.radio_male);
                    }
                    else
                    {
                        radioButton = findViewById(R.id.radio_female);
                    }
                    radioButton.setChecked(true);
                }
                else
                {
                    referenceProfile2.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                            if(readUserDetails != null)
                            {
                                name = readUserDetails.name;
                                DoB = readUserDetails.date_of_Birth;
                                gender = readUserDetails.gender;
                                blood = readUserDetails.blood_Group;
                                address = readUserDetails.address;
                                phone = readUserDetails.phone;

                                binding.inputName.setText(name);
                                binding.inputDate.setText(DoB);
                                binding.inputNumber.setText(phone);

                                if(gender.equals("Male"))
                                {
                                    radioButton =findViewById(R.id.radio_male);
                                }
                                else
                                {
                                    radioButton = findViewById(R.id.radio_female);
                                }
                                radioButton.setChecked(true);
                            }
                            else
                            {
                                Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
            Intent intent =new Intent(UpdateProfileActivity.this, MainActivity.class);
            startActivity(intent);
            //startActivity(getIntent());
            finish();
            //overridePendingTransition(0,0);
        }
        else if (id == R.id.menu_my_account)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_profile)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_email)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_settings)
        {
            Toast.makeText(UpdateProfileActivity.this, "Menu settings are on the list", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_change_password)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_delete_profile)
        {
            Intent intent =new Intent(UpdateProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_logout)
        {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(UpdateProfileActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
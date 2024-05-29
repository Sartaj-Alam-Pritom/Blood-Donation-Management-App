package com.example.blooddonationapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.MainActivity;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.databinding.ActivityDonaterBinding;
import com.example.blooddonationapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonaterActivity extends AppCompatActivity {

    private ActivityDonaterBinding binding;
    private String name,phone,email,password,category,DoB,address,pass2,selectedDivision,selectedzilla,selectedupzilla,gender;
    private String image;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar1;
    private RadioGroup radioGroupGender;
    private RadioButton radioButton;
    private Spinner zillaSpinner,upzillaSpinner,divisionSpinner,bloodSpinner;
    private ArrayAdapter<CharSequence> zillaAdapter,upzillaAdapter,divisionAdapter,bloodAdapter;
    private boolean access_granted =true,dataLocked=false;
    private long access_until=0;

    private DatePickerDialog picker;
    private static final String TAG = "DonaterActivity";

    TextInputEditText textInputEditText;
    TextInputLayout layoutPassword;

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonaterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonaterActivity.this,LoginActivity.class));
            }
        });

        button = findViewById(R.id.registerButton);
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

        progressBar1 = findViewById(R.id.progressbarID1);
        layoutPassword = findViewById(R.id.layoutPassword);
        textInputEditText = findViewById(R.id.inputPassword);

        auth = FirebaseAuth.getInstance();

        bloodAdapter = ArrayAdapter.createFromResource(this,R.array.array_blood,R.layout.spinner_layout);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(bloodAdapter);
        bloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=bloodSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String passwordInput = s.toString();
                    if(passwordInput.length()>=8)
                    {
                        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                        Matcher matcher = pattern.matcher(passwordInput);
                        boolean passwordsMatch = matcher.find();
                        if(passwordsMatch)
                        {
                            layoutPassword.setHelperText("Your Password is Strong");
                            layoutPassword.setError("");
                        }
                        else
                        {
                            layoutPassword.setError("Please enter mix of Letters(upper and lower case), Number and Symbols");
                        }
                    }
                    else
                    {
                        layoutPassword.setHelperText("Password must 8 Characters long");
                        layoutPassword.setError("");
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupGender.clearCheck();

        binding.inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(DonaterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.inputDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year,month,day);
                picker.show();
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = binding.inputName.getText().toString();
                phone = binding.inputNumber.getText().toString();
                email = binding.inputEmail.getText().toString();
                password = binding.inputPassword.getText().toString();
                DoB = binding.inputDate.getText().toString();
                pass2=binding.matchPassword.getText().toString();

                int selectedGenderId= radioGroupGender.getCheckedRadioButtonId();
                radioButton = findViewById(selectedGenderId);

                if(name.isEmpty())
                {
                    binding.inputName.setError("Type your name");
                    binding.inputName.requestFocus();
                }
                else if(email.isEmpty())
                {
                    binding.inputEmail.setError("Type your email");
                    binding.inputEmail.requestFocus();
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
                else if(category.equals("Select Your Blood Group"))
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
                else if(password.isEmpty())
                {
                    binding.inputPassword.setError("Type your password");
                    binding.inputPassword.requestFocus();
                }
                else if(pass2.isEmpty())
                {
                    binding.inputPassword.setError("Type your confirm password");
                    binding.inputPassword.requestFocus();
                }
                else if(!password.equals(pass2))
                {
                    binding.matchPassword.setError("Enter correct confirm password");
                    binding.matchPassword.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    binding.inputEmail.setError("Enter valid Email Address");
                    binding.inputEmail.requestFocus();
                }
                else
                {
                    progressBar1.setVisibility(View.VISIBLE);
                    gender=radioButton.getText().toString();
                    registerUser(name,email,phone,category,password,gender,DoB,address,access_granted,access_until,dataLocked,image);
                }
            }
        });

    }

    private void registerUser(String name,String email,String phone,String category,String password,String gender,String DoB,String address,boolean access_granted,long access_until,boolean dataLocked,String image)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DonaterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(DonaterActivity.this, "Donor Registration Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(name,email,phone,category,password,gender,DoB,address,access_granted,access_until,dataLocked,image);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");

                    assert firebaseUser != null;
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(DonaterActivity.this, "Donor Registration Successful.Please verify your Email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DonaterActivity.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                progressBar1.setVisibility(View.GONE);
                            }
                            else
                            {
                                Toast.makeText(DonaterActivity.this, "Donor Registration Failed.Please verify your Email", Toast.LENGTH_SHORT).show();
                            }
                            progressBar1.setVisibility(View.GONE);
                        }
                    });

                }
                else
                {
                    try{
                        throw Objects.requireNonNull(task.getException());
                    }
                    catch(FirebaseAuthWeakPasswordException e)
                    {
                        binding.inputPassword.setError("Your Password is too Weak.Kindly use a mix of Alphabets,Numbers and Special Characters.");
                        binding.inputPassword.requestFocus();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                        binding.inputEmail.setError("Your Email is invalid or already in use.Kindly re-enter.");
                        binding.inputEmail.requestFocus();
                    }
                    catch(FirebaseAuthUserCollisionException e)
                    {
                        binding.inputEmail.setError("User is already registered with this Email.Use another email.");
                        binding.inputEmail.requestFocus();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(DonaterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                progressBar1.setVisibility(View.GONE);
            }
        });
    }
}
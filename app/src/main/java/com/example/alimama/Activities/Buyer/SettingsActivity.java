package com.example.alimama.Activities.Buyer;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEdtiText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextBtn;
    private Uri imageUri;
    private String myUrl="";
    private StorageReference storageProfilePictureRef;
    private String checker="";
    private StorageTask uploadTask;
    private Button securityQuestions;


   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        profileImageView = findViewById(R.id.settings_profile_pic);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userPhoneEdtiText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);
        profileChangeTextBtn=findViewById(R.id.profile_image_change_btn);
        closeTextBtn=findViewById(R.id.close_settings_btn);
        saveTextBtn=findViewById(R.id.update_settings_btn);
        securityQuestions=findViewById(R.id.security_questions_btn);

        securityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,ForgotPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });
        
        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEdtiText,addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phone",userPhoneEdtiText.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Information Successfully Updated, Please Login Again", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error : Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Full Name is Mandatory", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPhoneEdtiText.getText().toString()))
        {
            Toast.makeText(this, "Phone Number is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri !=null)
        {
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                            HashMap<String,Object> userMap = new HashMap<>();
                            userMap.put("name",fullNameEditText.getText().toString());
                            userMap.put("address",addressEditText.getText().toString());
                            userMap.put("phone",userPhoneEdtiText.getText().toString());
                            userMap.put("image",myUrl);
                            ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                            progressDialog.dismiss();
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile Information Successfully Updated, Please Login Again", Toast.LENGTH_SHORT).show();
                            finish();


                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image has not been selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEdtiText, final EditText addressEditText) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(
        Prevalent.currentOnlineUser.getPhone());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEdtiText.setText(phone);
                        addressEditText.setText(address);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/
}
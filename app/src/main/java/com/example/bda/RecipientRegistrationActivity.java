package com.example.bda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipientRegistrationActivity extends AppCompatActivity {

    private TextView backtext;
    private CircleImageView profileimage;

    private TextInputEditText RegisterFullName,RegisterIdNumber,
            RegisterPhoneNumber,RegisterEmail,RegisterPassword;

    private Spinner BloodGroupSpinner;

    private Button RegisterButton;
    private Uri resultUri;

    private ProgressDialog Loader;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_registration);

        backtext = findViewById(R.id.back_text);
        profileimage = findViewById(R.id.profile_image);
        RegisterFullName = findViewById(R.id.registerFullName);
        RegisterIdNumber = findViewById(R.id.registerIdNumber);
        RegisterPhoneNumber = findViewById(R.id.registerPhoneNumber);
        RegisterEmail = findViewById(R.id.registerEmail);
        RegisterPassword = findViewById(R.id.registerPassword);
        BloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        RegisterButton = findViewById(R.id.registerButton);
        Loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


       backtext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),LoginActivity.class));
           }
       });
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = RegisterEmail.getText().toString().trim();
                final String password = RegisterPassword.getText().toString().trim();
                final String fullName = RegisterFullName.getText().toString().trim();
                final String idNumber = RegisterIdNumber.getText().toString().trim();
                final String phoneNumber = RegisterPhoneNumber.getText().toString().trim();
                final String bloodGroup = BloodGroupSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(email)){
                    RegisterEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    RegisterPassword.setError("Password is Required");
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    RegisterFullName.setError("Full Name is Required");
                    return;
                }
                if(TextUtils.isEmpty(idNumber)){
                    RegisterIdNumber.setError("ID Number is Required");
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    RegisterPhoneNumber.setError("Phone Number is Required");
                    return;
                }
                if(bloodGroup.equals("Select Your Blood Groups")){
                    Toast.makeText(RecipientRegistrationActivity.this, "Select Blood group", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Loader.setMessage("Registering You");
                    Loader.setCanceledOnTouchOutside(false);
                    Loader.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                String error = task.getException().toString();
                                Toast.makeText(RecipientRegistrationActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("name",fullName);
                                userInfo.put("email",email);
                                userInfo.put("idnumber",idNumber);
                                userInfo.put("phonenumber",phoneNumber);
                                userInfo.put("bloodgroup",bloodGroup);
                                userInfo.put("type","recipient");
                                userInfo.put("search","recipient"+bloodGroup);

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RecipientRegistrationActivity.this, "Data Set Successfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        //Loader.dismiss();
                                    }
                                });

                            if(resultUri != null){
                                final StorageReference filepath = FirebaseStorage.getInstance().getReference()
                                        .child("profile images").child(currentUserId);
                                Bitmap bitmap = null;
                                try{
                                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                byte[] data = byteArrayOutputStream.toByteArray();
                                UploadTask uploadTask = filepath.putBytes(data);

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RecipientRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if(taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    Map newImageMap = new HashMap();
                                                    newImageMap.put("profilepictureUrl",imageUrl);

                                                    userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(RecipientRegistrationActivity.this, "Image url added to database Successfully", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });
                                Intent intent = new Intent(RecipientRegistrationActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                                Loader.dismiss();
                            }
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode == RESULT_OK && data !=null){
            resultUri = data.getData();
            profileimage.setImageURI(resultUri);
        }
    }
}
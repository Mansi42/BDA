package com.example.bda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView Type,Name,Email,IdNumber,PhoneNumber,Bloodgroup;
    private CircleImageView ProfileImage;
    private Button Back_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Type = findViewById(R.id.type);
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        IdNumber = findViewById(R.id.idNumber);
        PhoneNumber = findViewById(R.id.phoneNumber);
        Bloodgroup = findViewById(R.id.bloodGroup);
        ProfileImage = findViewById(R.id.profileImageP);
        Back_Button = findViewById(R.id.BackButton);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Type.setText(snapshot.child("type").getValue().toString());
                    Name.setText(snapshot.child("name").getValue().toString());
                    IdNumber.setText(snapshot.child("idnumber").getValue().toString());
                    PhoneNumber.setText(snapshot.child("phonenumber").getValue().toString());
                    Bloodgroup.setText(snapshot.child("bloodgroup").getValue().toString());
                    Email.setText(snapshot.child("email").getValue().toString());

                    Glide.with(getApplicationContext()).load(snapshot.child("profilepictureUrl").getValue().toString()).into(ProfileImage);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
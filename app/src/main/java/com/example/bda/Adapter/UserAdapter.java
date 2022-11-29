package com.example.bda.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bda.Email.JavaMailApi;
import com.example.bda.Model.User;
import com.example.bda.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.user_displayed_layout,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);

        holder.Type.setText(user.getType());
        if(user.getType().equals("donor")){
            holder.EmailNow.setVisibility(View.VISIBLE);
            /*holder.CallNow.setVisibility(View.VISIBLE);*/
        }

        holder.UserEmail.setText(user.getEmail());
        holder.PhoneNumber.setText(user.getPhonenumber());
        holder.UserName.setText(user.getName());
        holder.BloodGroup.setText(user.getBloodgroup());

        Glide.with(context).load(user.getProfilepictureUrl()).into(holder.UserProfileImage);

        final String nameOfTheReceiver = user.getName();
        final String idOfTheReceiver = user.getId();

        //sending the email

        holder.EmailNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("SEND EMEIL")
                        .setMessage("Send Mail to " +user.getName()+ "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String nameOfSender = snapshot.child("name").getValue().toString();
                                        String email = snapshot.child("email").getValue().toString();
                                        String phone = snapshot.child("phonenumber").getValue().toString();
                                        String blood = snapshot.child("bloodgroup").getValue().toString();

                                        String mEmail = user.getEmail();
                                        String msubject = "BLOOD DONATION";
                                        String mMessage = "Hello "+ nameOfTheReceiver + ", "+nameOfSender +
                                                " would like blood donation from you. Here's his/her details:\n"
                                                +"Name: "+nameOfSender+ "\n"+
                                                "Phone Number: "+phone+ "\n"+
                                                "Email: "+email+ "\n"+
                                                "Blood Group: "+blood+ "\n"+
                                                "Kindly Reach out to him/her. Thank you!\n"
                                                +"BLOOD DONATION APP - DONATE BLOOD SO THAT OTHERS MAY LIVE!";

                                        JavaMailApi javaMailApi = new JavaMailApi(context,mEmail,msubject,mMessage);
                                        javaMailApi.execute();

                                        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("emails")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        senderRef.child(idOfTheReceiver).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("emails")
                                                            .child(idOfTheReceiver);
                                                    receiverRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                    addNotifications(idOfTheReceiver, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView UserProfileImage;
        public TextView Type, UserName, UserEmail, PhoneNumber, BloodGroup;
        public Button EmailNow,CallNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UserProfileImage = itemView.findViewById(R.id.userProfileImage);
            Type = itemView.findViewById(R.id.type);
            UserName = itemView.findViewById(R.id.userName);
            UserEmail = itemView.findViewById(R.id.userEmail);
            PhoneNumber = itemView.findViewById(R.id.phoneNumber);
            BloodGroup = itemView.findViewById(R.id.bloodGroup);
            EmailNow = itemView.findViewById(R.id.emailNow);
            /*CallNow = itemView.findViewById(R.id.callNow);*/
        }
    }
    private void addNotifications(String receiverId, String senderId){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("notifications").child(receiverId);

        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("receiverId", receiverId);
        hashMap.put("senderId", senderId);
        hashMap.put("text", "Sent you an email, kindly check it out");
        hashMap.put("date",date);

        reference.push().setValue(hashMap);
    }
}

package com.example.ecommerce20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinbtn,loginbtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinbtn = (Button) findViewById(R.id.join_btn);
        loginbtn = (Button) findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);;

        Paper.init(this);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        String Userphonekey =Paper.book().read(Prevalent.userphonekey);
        String Userpasswordkey =Paper.book().read(Prevalent.userpasswordkey);

        if(Userphonekey != ""  &&  Userpasswordkey != "")
        {
            if(!TextUtils.isEmpty(Userphonekey) && !TextUtils.isEmpty(Userpasswordkey))
            {
                AllowAccess(Userphonekey,Userpasswordkey);

                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(phone).exists())
                {
                    Users userData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent =new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.CurrentOnlineUser = userData;
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Account with this phone number does not Exist",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.example.ecommerce20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private EditText inputphone,inputpassword;
    private ProgressDialog loadingBar;
    private String parentDbName="Users";
    private CheckBox rememberme;
    private TextView adminlink,notadminlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        inputphone =(EditText) findViewById(R.id.number_login);
        inputpassword =(EditText) findViewById(R.id.editText2);
        loadingBar = new ProgressDialog(this);
        adminlink =findViewById(R.id.admin_panel);
        notadminlink= findViewById(R.id.not_admin_panel);

        rememberme = (CheckBox) findViewById(R.id.checkBox);
        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        adminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login.setText("Login Admin");
                adminlink.setVisibility(View.INVISIBLE);
                notadminlink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        notadminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login");
                adminlink.setVisibility(View.VISIBLE);
                notadminlink.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });
    }

    private void LoginUser()
    {
        String phone =inputphone.getText().toString();
        String password =inputpassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"enter your number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Details");
            loadingBar.setMessage("Please wait while we are checking for credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccess(phone,password);
        }
    }
    private void AllowAccess(final String phone, final String password)
    {
        if(rememberme.isChecked())
        {
            Paper.book().write(Prevalent.userphonekey,phone);
            Paper.book().write(Prevalent.userpasswordkey,password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {

                    Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone))
                     {
                        if (userData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this,"Welcome Admin, You're Logged In Successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.CurrentOnlineUser = userData;
                                startActivity(intent);
                            }

                        }
                        else
                            {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        {
                        Toast.makeText(LoginActivity.this, "Account with this phone number does not Exist", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

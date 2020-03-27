package com.example.ecommerce20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createacc;
    private EditText inputname,inputphno,inputpass;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createacc = (Button) findViewById(R.id.Registeracc);
        inputname =(EditText) findViewById(R.id.register_name);
        inputphno =(EditText) findViewById(R.id.number_login);
        inputpass =(EditText) findViewById(R.id.editText2);
        loadingBar = new ProgressDialog(this);

        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount()
    {
        String name =inputname.getText().toString();
        String phone =inputphno.getText().toString();
        String password =inputpass.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"enter your name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"enter your number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateDetails(name,phone,password);
        }

    }
    private void ValidateDetails(final String name, final String phone , final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String , Object> userdatamap =new HashMap<>();
                    userdatamap.put("name",name);
                    userdatamap.put("phone",phone);
                    userdatamap.put("password",password);

                    RootRef.child("Users").child(phone).updateChildren(userdatamap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this,"congratulations you have registered Successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent =new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this,"Network Error,Please Try again",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"This phone number is already Registered",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please Try another number",Toast.LENGTH_SHORT).show();

                    Intent intent =new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

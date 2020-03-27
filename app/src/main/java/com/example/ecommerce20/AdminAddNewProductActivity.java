package com.example.ecommerce20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName,Description,Price,Pname,saveCurrentTime,saveCurrentDate;
    private Button AddNewProduct;
    private ImageView productimage;
    private EditText ProductName,ProductDescription,ProductPrice;
    private static final int gallerypick= 1;
    private ProgressDialog loadingBar;
    private Uri ImageUri;
    private String ProductRandomKey,downloadImageUrl;

    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProduct = (Button)findViewById(R.id.product_add);
        productimage = (ImageView)findViewById(R.id.select_product_image);
        ProductName= (EditText) findViewById(R.id.pro_name);
        ProductDescription= (EditText) findViewById(R.id.pro_description);
        ProductPrice= (EditText) findViewById(R.id.pro_price);
        loadingBar = new ProgressDialog(this);

        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();
            }
        });
    }

    private void openGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallerypick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            productimage.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData()
    {
        Description =ProductDescription.getText().toString();
        Price=ProductPrice.getText().toString();
        Pname = ProductName.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this,"Image is Not Selected ",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"Please Enter Product Description ",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,"Please Enter Product Price",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this,"Please Enter Product Name ",Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInfo();
        }
    }

    private void StoreProductInfo()
    {

        loadingBar.setTitle("Add new Product");
        loadingBar.setMessage("Please wait while we are adding product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calender = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());

        ProductRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = ProductImageRef.child(ImageUri.getLastPathSegment() + ProductRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this,"Error : "+ message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }) .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl =  task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this,"got Product Image URl successfully",Toast.LENGTH_SHORT).show();
                            saveProductDataInfo();
                        }
                    }
                });

            }
        });

    }

    private void saveProductDataInfo() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", ProductRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("Image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("description", Description);
        productMap.put("price", Price);
        productMap.put("name", Pname);

        ProductRef.child(ProductRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {


                            Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this,"Product is Registered successfully",Toast.LENGTH_SHORT).show();


                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

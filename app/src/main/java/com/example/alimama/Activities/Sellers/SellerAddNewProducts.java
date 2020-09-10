package com.example.alimama.Activities.Sellers;

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

import com.example.alimama.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProducts extends AppCompatActivity {
    String categoryName,pname,pdescription,pprice,saveDate,saveTime;
    Button addNewProduct;
    EditText inputName,inputDescription, inputPrice;
    ImageView inputImage;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    String productRandomKey,downloadImageUrl;
    StorageReference productImagesRef;
    private DatabaseReference productsRef, sellersRef;
    ProgressDialog loadingBar;
    private String sName, sPhone, sEmail, sAddress,sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
       sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");


        addNewProduct = findViewById(R.id.add_new_product_btn);
        inputImage=findViewById(R.id.select_product_image);
        inputName=findViewById(R.id.product_name);
        inputPrice=findViewById(R.id.product_price);
        inputDescription=findViewById(R.id.product_description);
        loadingBar=new ProgressDialog(this);

        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });

        sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            sName = snapshot.child("name").getValue().toString();
                            sAddress = snapshot.child("address").getValue().toString();
                            sPhone = snapshot.child("phone").getValue().toString();
                            sEmail= snapshot.child("email").getValue().toString();
                            sID = snapshot.child("Sid").getValue().toString();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode == RESULT_OK && data !=null)
        {
            imageUri = data.getData();
            inputImage.setImageURI(imageUri);
        }
    }

    private void validateProductData() {
        pdescription = inputDescription.getText().toString();
        pprice = inputPrice.getText().toString();
        pname = inputName.getText().toString();

        if(imageUri == null)
        {
            Toast.makeText(SellerAddNewProducts.this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pdescription))
        {
            Toast.makeText(this,"Please write product description",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pprice))
        {
            Toast.makeText(this,"Please write product price",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pname))
        {
            Toast.makeText(this,"Please write product name",Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Please wait, while we are adding the new product..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveTime = currentTime.format(calendar.getTime());

        productRandomKey = saveDate+saveTime;
        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(SellerAddNewProducts.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProducts.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful())
                       {
                           throw task.getException();

                       }
                       downloadImageUrl = filePath.getDownloadUrl().toString();
                       return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(SellerAddNewProducts.this, "Product Image Successfully Saved to Database", Toast.LENGTH_SHORT).show();
                            saveProductInfotoDB();

                        }
                    }
                });

            }
        });


    }

    private void saveProductInfotoDB() {
        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveDate);
        productMap.put("time",saveTime);
        productMap.put("description",pdescription);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",pprice);
        productMap.put("name",pname);


        productMap.put("sellerName",sName);
        productMap.put("sellerAddress",sAddress);
        productMap.put("sellerEmail",sEmail);
        productMap.put("sellerPhone",sPhone);
        productMap.put("sellerId",sID);
        productMap.put("productStatus", "NOT_APPROVED");

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent (SellerAddNewProducts.this, SellerCategoryActivity.class);
                                startActivity(intent);
                                loadingBar.dismiss();
                                Toast.makeText(SellerAddNewProducts.this, "Product is Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            { loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(SellerAddNewProducts.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);

    }




}
package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class manualenlistpage extends AppCompatActivity {

    ImageView imageView,imageViewmini;
    Button save;
    EditText pname,proid,pdate;
    ImageButton reload,pdatepicker,autogen;
    TextView piddis;
    String currentPhotoPath,name,date,pid,dp,uid;
    Uri uri=null;
    DatePickerDialog datePickerDialog;
    DatabaseReference databaseReference;
    StorageReference mref;
    String dinms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualenlistpage);
        initpicker();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref= FirebaseStorage.getInstance().getReference();
        imageView=(ImageView) findViewById(R.id.enlist_productdp);
        imageViewmini=(ImageView) findViewById(R.id.enlistproductdp_small);
        pdatepicker=(ImageButton) findViewById(R.id.enlist_datepickerbtn);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        pdatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        autogen=(ImageButton) findViewById(R.id.enlist_autogenid);
        pname=(EditText) findViewById(R.id.enlist_productnamecode);
        pdate=(EditText) findViewById(R.id.enlistproductexpirycode);
        piddis=(TextView) findViewById(R.id.enlist_productidcode);
        proid=(EditText) findViewById(R.id.enlist_productidwriiten);
        save=(Button) findViewById(R.id.enlist_productsavecode);
        reload=(ImageButton) findViewById(R.id.enlist_refresh);
        proid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                piddis.setText(proid.getText().toString().trim());
                if(proid.getText().toString().isEmpty()){
                    piddis.setText("product number");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(manualenlistpage.this,manualenlistpage.class);
                startActivity(i);
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });
        imageViewmini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });
        autogen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                piddis.setText(databaseReference.push().getKey());
                proid.setText(databaseReference.push().getKey());
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveval(view);
            }
        });
    }

    public void saveval(View view){
        name = pname.getText().toString().trim();
        date = pdate.getText().toString().trim();
        pid = proid.getText().toString().trim();
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        if(!name.isEmpty() && !date.isEmpty() && !pid.isEmpty()){
            DatabaseReference mDatabase;
            mDatabase= FirebaseDatabase.getInstance().getReference();
            String randomkey = mDatabase.push().getKey();
            progressDialog.show();
            if (uri == null) {
                dp = "https://firebasestorage.googleapis.com/v0/b/hackathonproj-b48e0.appspot.com/o/apps_pics%2Ffooddefaultpic.jpg?alt=media&token=b1b90584-fdb7-476b-ad6a-7ad5dd2b6c9d";
                savetodb(dp, randomkey, mDatabase, progressDialog);
            } else {
                StorageReference filepath = mref.child("users").child(uid).child("Inventory").child(randomkey);
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String fileLink = task.getResult().toString();
                                        savetodb(fileLink, randomkey, mDatabase, progressDialog);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(manualenlistpage.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
        else {
            progressDialog.cancel();
            Toast.makeText(this,"Fields cant be empty",Toast.LENGTH_LONG).show();
        }
    }


    private void savetodb(String fileLink, String randomkey, DatabaseReference mDatabase, ProgressDialog progressDialog){
        productmodel productmodel = new productmodel(randomkey, proid.getText().toString().trim());
        productmodel.setDp(fileLink);
        productmodel.setName(name);
        productmodel.setUserid(uid);
        productmodel.setExpiry(date);
        productmodel.setExpiryinms(dinms);
        productmodel.setDate(System.currentTimeMillis() + "");
        mDatabase.child("users").child(uid).child("inventory").child(randomkey).setValue(productmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(manualenlistpage.this, "Successfull", Toast.LENGTH_LONG).show();
                Intent i=new Intent(manualenlistpage.this,startpage.class);
                startActivity(i);
                finish();
                progressDialog.cancel();
            }
        });
    }


    private void initpicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                Calendar calendar = Calendar.getInstance();
                calendar.set(view.getYear(), view.getMonth(), view.getDayOfMonth());
                long startTime = calendar.getTimeInMillis();
                dinms=String.valueOf(startTime);
                date = makeDateString(dayOfMonth, month, year);
                pdate.setText(date);
            }

        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        cal.add(Calendar.YEAR, 0); // to get previous year add 1
        cal.add(Calendar.DAY_OF_MONTH, 0); // to get previous day add -1
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                pdate.setText(day + " " + month + " " + year);
            }
        });
    }

    private String makeDateString(int day, int month, int year) {
        return day + " " +month+" " + year;
    }


    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    private void openDatePicker()
    {
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    File f=new File(currentPhotoPath);
                    uri= Uri.fromFile(f);
                    Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.fooddefaultpic)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri).placeholder(R.drawable.fooddefaultpic).into(imageView);
                                }
                            });
                    Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.fooddefaultpic)
                            .into(imageViewmini, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri).placeholder(R.drawable.fooddefaultpic).into(imageViewmini);
                                }
                            });
                    break;
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_Temp_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.harry9425.perish.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
}
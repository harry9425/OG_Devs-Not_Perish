package com.harry9425.notperish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
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
import com.google.zxing.Result;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class barcodescan extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    TextView productid;
    EditText pname, pdate, pcategory;
    Button save;
    ImageView imageView;
    ImageButton refresh;
    StorageReference mref;
    Uri uri = null;
    String selval;
    private DatePickerDialog datePickerDialog;
    String name, date, category, dp, uid, currentPhotoPath, pid = "",dinms;
    ArrayList<productmodel> ocrlist = MainActivity.arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodescan);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        pname = (EditText) findViewById(R.id.productnamecode);
        pdate = (EditText) findViewById(R.id.productexpirycode);
        pcategory = (EditText) findViewById(R.id.productcategorycode);
        productid = (TextView) findViewById(R.id.productidcode);
        mref = FirebaseStorage.getInstance().getReference();
        ;
        save = (Button) findViewById(R.id.productsavecode);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        imageView = (ImageView) findViewById(R.id.productdpcode);
        refresh = (ImageButton) findViewById(R.id.productrefreshcode);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        productid.setText(result.getText());
                    }
                });
            }
        });
        initpicker();
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        pdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        ImageButton calenderbtn = (ImageButton) findViewById(R.id.datepickerbtn);
        calenderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        if (MainActivity.fromocr == 1) {
            if (MainActivity.fromocr + 1 == ocrlist.size()) {
                save.setText("Save and exit");
            } else {
                save.setText("Save and next");
            }
            pname.setText(ocrlist.get(MainActivity.cnt).getName());
            pdate.setText(ocrlist.get(MainActivity.cnt).getExpiry());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    public void bkbk(View view) {
        onBackPressed();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void saveval(View view) {
        name = pname.getText().toString().trim();
        date = pdate.getText().toString().trim();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        if (!name.isEmpty() && !date.isEmpty()) {

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String randomkey = mDatabase.push().getKey();
            if (productid.getText().toString().equals("product number")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View view2 = getLayoutInflater().inflate(R.layout.enlistbox, null);
                builder.setView(view2);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                ImageButton cancel = view2.findViewById(R.id.enlistbox_cancel);
                ImageButton scan = view2.findViewById(R.id.elistbox_scan);
                ImageButton save = view2.findViewById(R.id.enlistbox_savedetail);
                Button generaterandom = view2.findViewById(R.id.enlistboc_randomqr);
                Button addmanual = view2.findViewById(R.id.enlistboc_qrbtn);
                EditText qrcodebyuser = view2.findViewById(R.id.enlistbox_qruser);
                TextView qrcodedisplay = view2.findViewById(R.id.enlistbox_pid);
                qrcodebyuser.setVisibility(View.GONE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        alertDialog.cancel();
                    }
                });
                scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        alertDialog.cancel();
                    }
                });
                qrcodebyuser.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        qrcodedisplay.setText(qrcodebyuser.getText().toString().trim());
                        selval=qrcodebyuser.getText().toString().trim();
                        if (qrcodebyuser.getText().toString().isEmpty()) {
                            qrcodedisplay.setText("product number");
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        productid.setText(selval);
                        alertDialog.dismiss();
                        alertDialog.cancel();
                    }
                });
                addmanual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        generaterandom.setAlpha(1f);
                        generaterandom.setTextColor(Color.parseColor("#43abb6"));
                        generaterandom.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        if (addmanual.getAlpha() == 1f) {
                            addmanual.setAlpha(0.98f);
                            addmanual.setBackgroundColor(Color.parseColor("#43abb6"));
                            addmanual.setTextColor(Color.parseColor("#FFFFFF"));
                            qrcodebyuser.setVisibility(View.VISIBLE);
                        } else {
                            addmanual.setAlpha(1f);
                            qrcodebyuser.setVisibility(View.GONE);
                            addmanual.setTextColor(Color.parseColor("#43abb6"));
                            addmanual.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                });
                generaterandom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addmanual.setAlpha(1f);
                        selval="";
                        addmanual.setTextColor(Color.parseColor("#43abb6"));
                        addmanual.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        qrcodebyuser.setVisibility(View.GONE);

                        if (generaterandom.getAlpha() == 1f) {
                            generaterandom.setAlpha(0.98f);
                            qrcodedisplay.setText(randomkey);
                            selval=randomkey;
                            generaterandom.setBackgroundColor(Color.parseColor("#43abb6"));
                            generaterandom.setTextColor(Color.parseColor("#FFFFFF"));
                        } else {
                            generaterandom.setAlpha(1f);
                            generaterandom.setTextColor(Color.parseColor("#43abb6"));
                            generaterandom.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                });
                alertDialog.show();

            } else {
                progressDialog.show();
                if (uri == null) {
                    dp = "https://firebasestorage.googleapis.com/v0/b/notperish-a887a.appspot.com/o/defaultfoodpic.jpeg?alt=media&token=5f0294d7-e3f5-4d7e-aedc-7cbb4b68db36";
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
                                    Toast.makeText(barcodescan.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        } else {
            progressDialog.cancel();
            Toast.makeText(this, "Fields cant be empty", Toast.LENGTH_LONG).show();
        }
    }

    private void savetodb(String fileLink, String randomkey, DatabaseReference mDatabase, ProgressDialog progressDialog) {
        productmodel productmodel = new productmodel(randomkey, productid.getText().toString());
        productmodel.setDp(fileLink);
        productmodel.setName(name);
        productmodel.setExpiryinms(dinms);
        productmodel.setUserid(uid);
        productmodel.setExpiry(date);
        productmodel.setDate(System.currentTimeMillis() + "");
        mDatabase.child("users").child(uid).child("inventory").child(randomkey).setValue(productmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(barcodescan.this, "Successfull", Toast.LENGTH_LONG).show();
                progressDialog.cancel();
                if (MainActivity.fromocr == 0) {
                    Intent i = new Intent(barcodescan.this, startpage.class);
                    startActivity(i);
                    finish();
                } else {
                    MainActivity.cnt++;
                    if (MainActivity.cnt < ocrlist.size()) {
                        Intent i = new Intent(barcodescan.this, barcodescan.class);
                        startActivity(i);
                        finish();
                    } else {
                        MainActivity.fromocr = 0;
                        Intent i = new Intent(barcodescan.this, startpage.class);
                        startActivity(i);
                        finish();
                    }
                }
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

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    private void openDatePicker() {
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    File f = new File(currentPhotoPath);
                    uri = Uri.fromFile(f);
                    Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.fooddefaultpic)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri).placeholder(R.drawable.fooddefaultpic).into(imageView);
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
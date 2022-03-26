package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static TextView textView;
    ImageView imageView;
    Bitmap bitmap;
    Uri outputUri;
    String currentPhotoPath;
    String ocrresult;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    FrameLayout frameLayout;
    Camera camera;
    public static int fromocr=0;
    public static int cnt=0;
    public static ArrayList<productmodel> arrayList;
    showcamera showcamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout=(FrameLayout) findViewById(R.id.frameLayout_ocr);
        camera=Camera.open();
        showcamera=new showcamera(this,camera);
        frameLayout.addView(showcamera);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera=Camera.open();
        showcamera=new showcamera(this,camera);
        frameLayout.addView(showcamera);
    }

    public  void takepic(View view){
        dispatchTakePictureIntent();
    }

    public void convertpic(){
        InputImage image=InputImage.fromBitmap(bitmap,0);
        TextRecognizer recognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result=new StringBuilder();
                String blocktext="";
                for(Text.TextBlock block:text.getTextBlocks()){
                    blocktext=blocktext+"\n"+block.getText();
                    Point[] blockcp=block.getCornerPoints();
                    Rect bloackframe=block.getBoundingBox();
                    for(Text.Line line:block.getLines()){
                        String linetxt=line.getText();
                        Point[] linecp=line.getCornerPoints();
                        Rect lineframe=line.getBoundingBox();
                        for(Text.Element element:line.getElements()){
                            String elementtext=element.getText();
                            result.append(elementtext);
                        }
                        ocrresult=blocktext;
                        getlistfromtext(ocrresult);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"error 504",Toast.LENGTH_LONG).show();
            }
        });
    }

    void getlistfromtext(String ocrtext){
        Toast.makeText(MainActivity.this,ocrtext,Toast.LENGTH_LONG).show();
        if(ocrtext.contains(" &E& ")){
            //String[] strings=ocrtext.split(".\n");
            arrayList =new ArrayList<>();
            for(String s:ocrtext.split(".\n")){
                s=s.replaceAll("\n"," ");
                String[] res=s.split("&E&");
                productmodel productmodel=new productmodel();
                productmodel.setName(res[0].trim());
                productmodel.setExpiry(res[1].trim());
                arrayList.add(productmodel);
            }
            fromocr=1;
            Intent i=new Intent(MainActivity.this,barcodescan.class);
            startActivity(i);
        }
        else {
            arrayList =new ArrayList<>();
            for(String s:ocrtext.split(".\n")){
                s =s.replaceAll("\n"," ");
                productmodel productmodel=new productmodel();
                productmodel.setName(s.trim());
                productmodel.setExpiry("");
                arrayList.add(productmodel);
            }
            fromocr=1;
            Intent i=new Intent(MainActivity.this,barcodescan.class);
            startActivity(i);
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

    private void dispatchTakePictureIntent() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    File f = new File(currentPhotoPath);
                    Uri uri = Uri.fromFile(f);
                    outputUri = uri;
                    CropImage.activity(outputUri)
                            .start(this);
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        outputUri = result.getUri();
                        try
                        {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , outputUri);
                            AlertDialog.Builder builder=new AlertDialog.Builder(this);
                            final View view2=getLayoutInflater().inflate(R.layout.scanalertbox,null);
                            builder.setView(view2);
                            AlertDialog alertDialog =builder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            ImageButton reload=view2.findViewById(R.id.refloadscanalert);
                            ImageButton cancel=view2.findViewById(R.id.cancelscanalert);
                            Button convert=view2.findViewById(R.id.continuescanalert);
                            ImageView imageView=view2.findViewById(R.id.imagescanalert);
                            reload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    alertDialog.cancel();
                                    File f = new File(currentPhotoPath);
                                    Uri uri = Uri.fromFile(f);
                                    CropImage.activity(uri)
                                            .start(MainActivity.this);
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    alertDialog.cancel();
                                }
                            });
                            Glide.with(this)
                                    .load(outputUri)
                                    .placeholder(R.drawable.logoloading)
                                    .into(imageView);
                            convert.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    convertpic();
                                }
                            });
                            alertDialog.show();
                        }
                        catch (Exception e)
                        {}
                        // imageView.setImageBitmap(bitmap);
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                    break;
            }
        }
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        ExifInterface exif = new ExifInterface(src);
        String orientationString=exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        try {
            orientation = Integer.parseInt(orientationString);
        }
        catch(NumberFormatException e){}
        return orientation;
    }

    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);
            textView.setText(orientation+"");
            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
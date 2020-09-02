package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class acc_creategame3 extends AppCompatActivity {

    Info info = new Info();
    private String mytitle;
    private EditText accEtQuestion, accEtAnsA, accEtAnsB, accEtAnsC, accEtAnsD, accEtHint;

    private Button btnGenQr, accsave;
    private ImageView qrimage;
    private FirebaseAuth mAuth;
    private Firebase mRef;

    Thread thread;
    public final static int QRcodeWidth = 1000;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_creategame3);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accsave = (Button) findViewById(R.id.accSave);
        accsave.setVisibility(View.GONE);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        Intent i = getIntent();
        final int position = i.getIntExtra("position", 0);
        Log.i("acc_creategame3","position:"+ Integer.toString(position));
        TextView tvquestionNum = (TextView)findViewById(R.id.tvquestionNum);
        tvquestionNum.setText("Question "+position);


        btnGenQr = (Button) findViewById(R.id.accGenQr);
        qrimage = (ImageView) findViewById(R.id.qrimage);

        RadioGroup accCorrectAnswerRadiogrp = (RadioGroup) findViewById(R.id.accCorrectAnswerRadiogrp);
        int selectedButtonId = accCorrectAnswerRadiogrp.getCheckedRadioButtonId();
        final RadioButton rb = (RadioButton) findViewById(selectedButtonId);
//        Log.i("acc_creategame3","rb:"+rb.getText().toString());
        accEtQuestion = (EditText) findViewById(R.id.accEtQuestion);
        accEtAnsA = (EditText) findViewById(R.id.accEtAnsA);
        accEtAnsB = (EditText) findViewById(R.id.accEtAnsB);
        accEtAnsC = (EditText) findViewById(R.id.accEtAnsC);
        accEtAnsD = (EditText) findViewById(R.id.accEtAnsD);
        accEtHint = (EditText) findViewById(R.id.accEtHint);

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                String title = map.get("tmp game title");
                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + title).child(Integer.toString(position));
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                        if (dataSnapshot.exists()) {
                            Log.i("acc_creategame3","exist");
                            String a = map.get("ansa");
                            String b = map.get("ansb");
                            String c = map.get("ansc");
                            String d = map.get("ansd");
                            String correctAns = map.get("correctAns");
                            String hint = map.get("hint");
                            String question = map.get("question");
                            accEtQuestion.setText(question);
                            accEtAnsA.setText(a);
                            accEtAnsB.setText(b);
                            accEtAnsC.setText(c);
                            accEtAnsD.setText(d);
                            accEtHint.setText(hint);
                            RadioButton rba = (RadioButton)findViewById(R.id.rba);
                            RadioButton rbb = (RadioButton)findViewById(R.id.rbb);
                            RadioButton rbc = (RadioButton)findViewById(R.id.rbc);
                            RadioButton rbd = (RadioButton)findViewById(R.id.rbd);
                            if(correctAns.equals("A")){
                                rba.setChecked(true);
                            }else if (correctAns.equals("B")){
                                rbb.setChecked(true);
                            }else if (correctAns.equals("C")){
                                rbc.setChecked(true);
                            }else if (correctAns.equals("D")){
                                rbd.setChecked(true);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        accsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }


                storeImage(bitmap, position);
            }
        });

        btnGenQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup accCorrectAnswerRadiogrp = (RadioGroup) findViewById(R.id.accCorrectAnswerRadiogrp);
                int selectedButtonId = accCorrectAnswerRadiogrp.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedButtonId);
                Log.i("acc_creategame3","question:"+ accEtQuestion.getText().toString().length() + "");
                Log.i("acc_creategame3","accEtAnsA:"+accEtAnsA.getText().toString().length() + "");
                Log.i("acc_creategame3","accEtAnsB:"+accEtAnsB.getText().toString().length() + "");
                Log.i("acc_creategame3","accEtAnsC:"+ accEtAnsC.getText().toString().length() + "");
                Log.i("acc_creategame3","accEtAnsD:"+ accEtAnsD.getText().toString().length() + "");
                Log.i("acc_creategame3","accEtHint:"+accEtHint.getText().toString().length() + "");
                if (accEtQuestion.getText().toString().length() > 0 && accEtAnsA.getText().toString().length() > 0 && accEtAnsB.getText().toString().length() > 0 && accEtAnsC.getText().toString().length() > 0 && accEtAnsD.getText().toString().length() > 0 && accEtHint.getText().toString().length() > 0 && selectedButtonId !=-1) {

                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game");
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, String> map = dataSnapshot.getValue(Map.class);
                            RadioGroup accCorrectAnswerRadiogrp = (RadioGroup) findViewById(R.id.accCorrectAnswerRadiogrp);
                            int selectedButtonId = accCorrectAnswerRadiogrp.getCheckedRadioButtonId();
                            RadioButton rb = (RadioButton) findViewById(selectedButtonId);
                            String title = map.get("tmp game title");
                            String time = map.get("tmp time");
                            String totallocation = map.get("tmp location");
                            info.setGame_title(title);
                            Firebase childReftitle = mRef.child(title);
                            childReftitle.child("time").setValue(time);
                            childReftitle.child("total location").setValue(totallocation);

                            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + title);
                            Firebase childRef = mRef.child(Integer.toString(position));
                            childRef.child("id").setValue(position);
                            childRef.child("question").setValue(accEtQuestion.getText().toString());
                            childRef.child("ansa").setValue(accEtAnsA.getText().toString());
                            childRef.child("ansb").setValue(accEtAnsB.getText().toString());
                            childRef.child("ansc").setValue(accEtAnsC.getText().toString());
                            childRef.child("ansd").setValue(accEtAnsD.getText().toString());
                            childRef.child("hint").setValue(accEtHint.getText().toString());
                            childRef.child("correctAns").setValue(rb.getText().toString());


                            try {
                                bitmap = TextToImageEncode(position+"/"+accEtQuestion.getText().toString().trim() + "/" + accEtAnsA.getText().toString().trim() + "/" + accEtAnsB.getText().toString().trim() + "/" + accEtAnsC.getText().toString().trim() + "/" + accEtAnsD.getText().toString().trim() + "/" + accEtHint.getText().toString().trim() + "/" + rb.getText().toString().trim());

                                qrimage.setImageBitmap(bitmap);
                                accsave.setVisibility(View.VISIBLE);


                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                } else {
                    Toast.makeText(acc_creategame3.this, "Please enter all field", Toast.LENGTH_LONG).show();

                }

                //qrcode




            }


        });
    }


    private void storeImage(Bitmap image, int position) {
        File pictureFile = getOutputMediaFile(position);
        if (pictureFile == null) {
            Log.d("acc_creategame3", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("acc_creategame3", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("acc_creategame3", "Error accessing file: " + e.getMessage());
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int position) {
// To be safe, you should check that the SDCard is mounted
// using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new
                File(Environment.getExternalStorageDirectory()
                + "/QRHunt");

// This location works best if you want the created images to be shared
// between applications and persist after your app has been uninstalled.

// Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Toast.makeText(this,"succ",Toast.LENGTH_LONG).show();
                return null;
            }
        }
// Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        Log.i("tttinfo", info.getGame_title());
        String mImageName = "QRHunt_" + info.getGame_title() + "_" + position + "_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Toast.makeText(acc_creategame3.this, mediaFile.getPath() + "", Toast.LENGTH_LONG).show();
        return mediaFile;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 1000, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


}










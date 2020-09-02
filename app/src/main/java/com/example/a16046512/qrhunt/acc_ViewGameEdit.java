package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class acc_ViewGameEdit extends AppCompatActivity {

    Info info = new Info();
    private EditText accEtQuestionEdit, accEtAnsAEdit, accEtAnsBEdit, accEtAnsCEdit, accEtAnsDEdit, accEtHintEdit;

    private Button btnGenQrEdit, accsaveEdit;
    private ImageView qrimageEdit;
    private FirebaseAuth mAuth;
    private Firebase mRef;

    Thread thread;
    public final static int QRcodeWidth = 1000;
    Bitmap bitmap;
    int pos;
    String cate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__view_game_edit);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        pos = i.getIntExtra("editgamepos", 0);
//        Log.i("acc_ViewGameEdit",pos+"");

        accsaveEdit = (Button) findViewById(R.id.accSaveEdit);
        accsaveEdit.setVisibility(View.GONE);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();


        TextView tvquestionNumEdit = (TextView)findViewById(R.id.tvquestionNumEdit);
        tvquestionNumEdit.setText("Question "+Integer.toString(pos));


        btnGenQrEdit = (Button) findViewById(R.id.accGenQrEdit);
        qrimageEdit = (ImageView) findViewById(R.id.qrimageEdit);

        RadioGroup accCorrectAnswerRadiogrpEdit = (RadioGroup) findViewById(R.id.accCorrectAnswerRadiogrpEdit);
        int selectedButtonIdEdit = accCorrectAnswerRadiogrpEdit.getCheckedRadioButtonId();
        final RadioButton rbEdit = (RadioButton) findViewById(selectedButtonIdEdit);
//        Log.i("acc_ViewGameEdit",rb.getText().toString());
        accEtQuestionEdit = (EditText) findViewById(R.id.accEtQuestionEdit);
        accEtAnsAEdit = (EditText) findViewById(R.id.accEtAnsAEdit);
        accEtAnsBEdit = (EditText) findViewById(R.id.accEtAnsBEdit);
        accEtAnsCEdit = (EditText) findViewById(R.id.accEtAnsCEdit);
        accEtAnsDEdit = (EditText) findViewById(R.id.accEtAnsDEdit);
        accEtHintEdit = (EditText) findViewById(R.id.accEtHintEdit);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(acc_ViewGameEdit.this);
        cate = prefs.getString("cate","");
                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + cate).child(Integer.toString(pos));
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                        if (dataSnapshot.exists()) {
//                            Log.i("acc_ViewGameEdit","exist");
                            String a = map.get("ansa");
                            String b = map.get("ansb");
                            String c = map.get("ansc");
                            String d = map.get("ansd");
                            String correctAns = map.get("correctAns");
                            String hint = map.get("hint");
                            String question = map.get("question");
//                            Log.i("acc_ViewGameEdit","data:"+a+b+c+d+correctAns+hint+question);
                            accEtQuestionEdit.setText(question);
                            accEtAnsAEdit.setText(a);
                            accEtAnsBEdit.setText(b);
                            accEtAnsCEdit.setText(c);
                            accEtAnsDEdit.setText(d);
                            accEtHintEdit.setText(hint);
                            RadioButton rbaEdit = (RadioButton)findViewById(R.id.rbaEdit);
                            RadioButton rbbEdit = (RadioButton)findViewById(R.id.rbbEdit);
                            RadioButton rbcEdit = (RadioButton)findViewById(R.id.rbcEdit);
                            RadioButton rbdEdit = (RadioButton)findViewById(R.id.rbdEdit);
                            if(correctAns.equals("A")){
                                rbaEdit.setChecked(true);
                            }else if (correctAns.equals("B")){
                                rbbEdit.setChecked(true);
                            }else if (correctAns.equals("C")){
                                rbcEdit.setChecked(true);
                            }else if (correctAns.equals("D")){
                                rbdEdit.setChecked(true);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });




        accsaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }


                storeImage(bitmap, pos);
            }
        });

        btnGenQrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup accCorrectAnswerRadiogrp = (RadioGroup) findViewById(R.id.accCorrectAnswerRadiogrpEdit);
                int selectedButtonId = accCorrectAnswerRadiogrp.getCheckedRadioButtonId();
                RadioButton rbEdit = (RadioButton) findViewById(selectedButtonId);
//                Log.i("acc_ViewGameEdit","question:"+ accEtQuestionEdit.getText().toString().length() + "");
//                Log.i("acc_ViewGameEdit","accEtAnsA:"+ accEtAnsAEdit.getText().toString().length() + "");
//                Log.i("acc_ViewGameEdit","accEtAnsB:"+ accEtAnsBEdit.getText().toString().length() + "");
//                Log.i("acc_ViewGameEdit","accEtAnsC:"+ accEtAnsCEdit.getText().toString().length() + "");
//                Log.i("acc_ViewGameEdit","accEtAnsD:"+ accEtAnsDEdit.getText().toString().length() + "");
//                Log.i("acc_ViewGameEdit","accEtHint:"+ accEtHintEdit.getText().toString().length() + "");
                if (accEtQuestionEdit.getText().toString().length() > 0 && accEtAnsAEdit.getText().toString().length() > 0 && accEtAnsBEdit.getText().toString().length() > 0 && accEtAnsCEdit.getText().toString().length() > 0 && accEtAnsDEdit.getText().toString().length() > 0 && accEtHintEdit.getText().toString().length() > 0 && selectedButtonId !=-1) {

                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game");
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map<String, String> map = dataSnapshot.getValue(Map.class);
                            RadioGroup accCorrectAnswerRadiogrp = (RadioGroup) findViewById(R.id.accCorrectAnswerRadiogrpEdit);
                            int selectedButtonId = accCorrectAnswerRadiogrp.getCheckedRadioButtonId();
                            RadioButton rbEdit = (RadioButton) findViewById(selectedButtonId);
//                            String title = map.get("tmp game title");
//                            String time = map.get("tmp time");
//                            info.setGame_title(title);
//                            Firebase childReftitle = mRef.child(title);
//                            childReftitle.child("time").setValue(time);

                            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + cate);
                            Firebase childRef = mRef.child(Integer.toString(pos));
                            childRef.child("id").setValue(Integer.toString(pos));
                            childRef.child("question").setValue(accEtQuestionEdit.getText().toString());
                            childRef.child("ansa").setValue(accEtAnsAEdit.getText().toString());
                            childRef.child("ansb").setValue(accEtAnsBEdit.getText().toString());
                            childRef.child("ansc").setValue(accEtAnsCEdit.getText().toString());
                            childRef.child("ansd").setValue(accEtAnsDEdit.getText().toString());
                            childRef.child("hint").setValue(accEtHintEdit.getText().toString());
                            childRef.child("correctAns").setValue(rbEdit.getText().toString());


                            try {
                                bitmap = TextToImageEncode(Integer.toString(pos)+"/"+accEtQuestionEdit.getText().toString().trim() + "/" + accEtAnsAEdit.getText().toString().trim() + "/" + accEtAnsBEdit.getText().toString().trim() + "/" + accEtAnsCEdit.getText().toString().trim() + "/" + accEtAnsDEdit.getText().toString().trim() + "/" + accEtHintEdit.getText().toString().trim() + "/" + rbEdit.getText().toString().trim());

                                qrimageEdit.setImageBitmap(bitmap);
                                accsaveEdit.setVisibility(View.VISIBLE);


                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                } else {
                    Toast.makeText(acc_ViewGameEdit.this, "Please enter all field", Toast.LENGTH_LONG).show();

                }

                //qrcode




            }


        });
    }


    private void storeImage(Bitmap image, int position) {
        File pictureFile = getOutputMediaFile(position);
        if (pictureFile == null) {
            Log.d("acc_ViewGameEdit","Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("acc_ViewGameEdit", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("acc_ViewGameEdit", "Error accessing file: " + e.getMessage());
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
        Log.i("acc_ViewGameEdit","info:"+ info.getGame_title());
        String mImageName = "QRHunt_" + info.getGame_title() + "_" + position + "_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Toast.makeText(acc_ViewGameEdit.this, mediaFile.getPath() + "", Toast.LENGTH_LONG).show();
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










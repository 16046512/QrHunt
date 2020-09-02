package com.example.a16046512.qrhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.Random;

public class acc_StartGame extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Firebase mRef,mRef2;
    private String selectedgametitle;
    private TextView tvCode;

    String random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__start_game);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        tvCode = (TextView)findViewById(R.id.tvCode);

        Intent i = getIntent();
        selectedgametitle = i.getStringExtra("gametitle");
        Log.i("acc_StartGame","tttgametitle:"+selectedgametitle);

        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int N = alphabet.length();

        Random r = new Random();
        random = "";
        for (int a = 0; a < 10; a++) {
            random = random + alphabet.charAt(r.nextInt(N));
        }

        Log.i("acc_StartGame","tttrandom:"+random);







        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/"+selectedgametitle+"/code");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!dataSnapshot.exists()){
                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/");
                    Firebase childRef = mRef.child(selectedgametitle);
                    childRef.child("code").setValue(random);



                    mRef2 = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/");
                    Firebase childRef2 = mRef2.child(random);
                    childRef2.child("userid").setValue(user.getUid());
                    childRef2.child("title").setValue(selectedgametitle);
                    tvCode.setText(random);

                }else{


                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + random);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (!dataSnapshot.exists()){

                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/");
                                Firebase childRef = mRef.child(random);
                                childRef.child("userid").setValue(user.getUid());
                                childRef.child("title").setValue(selectedgametitle);
                                tvCode.setText(random);

                            }else{

                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




























    }





}

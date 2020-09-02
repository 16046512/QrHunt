package com.example.a16046512.qrhunt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;


public class acc_Game extends AppCompatActivity {

    //Firebase start
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase mRef;
    //Firebase end


    private CountDownTimer cDtimer;

    private boolean timeRunning;
    private TextView tvqrfound, tvquestioncorrect;
    private Button btnHint;
    private ImageButton btnCamera;
    private TextView display;
    private String time,guestuserid,guesttitle,guesttime,guestname;
     String numoflocation;

    private long remainingTime ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__game);

//        Intent i = getIntent();
//        String guestuserid = i.getStringExtra("guestuserid");
//        String guesttitle = i.getStringExtra("guesttitle");
//        String guesttime = i.getStringExtra("guesttime");
//        String guestname = i.getStringExtra("guestname");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
         guestuserid = pref.getString("guestuserid","");
         guesttitle = pref.getString("guesttitle","");
         guesttime = pref.getString("guesttime","");
         guestname = pref.getString("guestname","");
         Log.i("accgame",guestuserid+":"+guesttitle+":"+guesttime+":"+guestname);
        remainingTime = 60000 * Integer.parseInt(guesttime);
//        remainingTime = 60000 * 1;
        startTimer();
        Log.i("main",":"+guesttitle+":"+guesttime+":"+guestname);

        display = (TextView) findViewById(R.id.display);


        tvqrfound = (TextView) findViewById(R.id.tvqrfound);
        tvquestioncorrect = (TextView) findViewById(R.id.tvquestioncorrect);
        btnHint = (Button) findViewById(R.id.btnhint);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        final Activity activity = this;
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(acc_Game.this,acc_QRScan.class);
                startActivity(i);

            }
        });

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Game/" + guesttitle  );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                numoflocation = map.get("total location");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Player/" + guesttitle + "/" +guestname );
        Log.i("accGame104",mRef+"");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                final String guestqrfound = map.get("QR Found");
                final String guestquestioncorrect = map.get("Question Correct");
                tvqrfound.setText("QR Found: "+guestqrfound+"/"+numoflocation);
                tvquestioncorrect.setText("Question Correct"+guestquestioncorrect+"/"+numoflocation);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(acc_Game.this,Hint.class);
                startActivity(i);
            }
        });
    }



    public void updateTimer() {
        int hour = (int) remainingTime / 3600000;
        int minute = (int) (remainingTime / 60000) % 60;
        int second = (int) remainingTime % 60000 / 1000;
        String timeLeft;
        timeLeft = " Time left ";

        if (hour < 10) timeLeft += "0";
        timeLeft += hour + " : ";

        if (minute < 10) timeLeft += "0";
        timeLeft += minute + " : ";

        if (second < 10) timeLeft += "0";
        timeLeft += second;
        display.setText(timeLeft);


        if (hour == 0 && minute < 5) {
            display.setTextColor(Color.parseColor("#ff0000"));
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(200); //You can manage the time of the blink with this parameter
            anim.setStartOffset(200);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            display.startAnimation(anim);
        }

    }


    public void startTimer() {
        cDtimer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long l) {
                remainingTime = l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                Toast.makeText (acc_Game.this,"Game Ended. . . Thank you for playing ",Toast.LENGTH_LONG).show();
                finish();
            }
        }.start();
        timeRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
         guestuserid = pref.getString("guestuserid","");
         guesttitle = pref.getString("guesttitle","");
         guesttime = pref.getString("guesttime","");
         guestname = pref.getString("guestname","");

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Game/" + guesttitle  );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                numoflocation = map.get("total location");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Player/" + guesttitle + "/" +guestname );
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                final String guestqrfound = map.get("QR Found");
                final String guestquestioncorrect = map.get("Question Correct");
                tvqrfound.setText("QR Found: "+guestqrfound+"/"+numoflocation);
                tvquestioncorrect.setText("Question Correct"+guestquestioncorrect+"/"+numoflocation);
                if(Integer.parseInt(guestqrfound) == Integer.parseInt(numoflocation)){


                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Player/" + guesttitle + "/");
                    //addValueEventListener
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Firebase childRef = mRef.child(guestname);

                            int rt = (int)remainingTime;

                            int hour = (int) rt / 3600000;
                            int minute = (int) (rt / 60000) % 60;
                            int second = (int) rt % 60000 / 1000;



                            Log.i("acc_Game222",hour+":"+minute+":"+second+"");
                            int timepoint = (hour*100)+(minute*10)+second;
                            int total = Integer.parseInt(guestqrfound)+Integer.parseInt(numoflocation)+timepoint;
                            childRef.child("Score").setValue(Integer.toString(total));
                            Toast.makeText (acc_Game.this,"Game Ended. . . Thank you for playing ",Toast.LENGTH_LONG).show();
                            finish();

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

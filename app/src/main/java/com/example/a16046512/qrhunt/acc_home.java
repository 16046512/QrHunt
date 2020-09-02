package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class acc_home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView tvwelcome;
    private NavigationView nav_view;
    private EditText accEtCode;
    private Button accPlayGameBtn;
    //sidebar end

    private FirebaseAuth mAuth;
    private Firebase mRef;
    private Button creategame,accViewBtn;
    private String value,userid,time,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_home);

        //firebase start
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        Firebase.setAndroidContext(this);
        //firebase end

        //sidebar start
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);

        //set header text
        nav_view = (NavigationView) findViewById(R.id.navView);
        nav_view.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View header = nav_view.getHeaderView(0);
        tvwelcome = (TextView) header.findViewById(R.id.txtwelcome);

        creategame = (Button)findViewById(R.id.accCreateGameBtn);
        accViewBtn = (Button)findViewById(R.id.accViewBtn);
        accPlayGameBtn= (Button)findViewById(R.id.accPlayGameBtn);
        accEtCode= (EditText) findViewById(R.id.accEtCode);


        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/"+user.getUid()+"/name");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 value = dataSnapshot.getValue(String.class);
                tvwelcome.setText("Welcome "+value);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        creategame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),acc_creategame.class);
                startActivity(intent);
            }
        });



        accViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),acc_View.class);
                startActivity(intent);
            }
        });




        accPlayGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("acchome",accEtCode.getText().toString());
                if(accEtCode.getText().toString().equals("")){

                    Toast.makeText(acc_home.this,"Please enter code to enter game",Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(acc_home.this);
                    SharedPreferences.Editor prefEdit = prefs.edit();
                    prefEdit.putString("name", value);
                    prefEdit.putString("code", accEtCode.getText().toString());
                    prefEdit.commit();



                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + accEtCode.getText().toString());
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Map<String, String> map = dataSnapshot.getValue(Map.class);
                                 title = map.get("title");
                                userid = map.get("userid");
                                Log.i("MainActivity", title + ":" + userid);

                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Game/" + title );
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        time = map.get("time");
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });

                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + value);
                                Log.i("acc_home",""+mRef);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {

                                            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/"+title);
                                            Firebase childRef = mRef.child(value);
                                            childRef.child("Name").setValue(value);
                                            childRef.child("QR Found").setValue("0");
                                            childRef.child("Question Correct").setValue("0");
                                            childRef.child("NumOfQuestion").setValue("0/");
                                            childRef.child("Score").setValue("0");
                                            Intent i = new Intent(acc_home.this, acc_Game.class);
                                            Log.i("acc_home",userid+":"+title+":"+time+":"+value);
                                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(acc_home.this);
                                            SharedPreferences.Editor pe = sp.edit();
                                            pe.putString("guestuserid",userid);
                                            pe.putString("guesttitle",title);
                                            pe.putString("guesttime",time);
                                            pe.putString("guestname",value);
                                            pe.commit();

                                            startActivity(i);
                                        }



                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            } else {
                                Log.i("acc_home", "not exist");
                                Toast.makeText(acc_home.this, "This code does not exist", Toast.LENGTH_LONG).show();
                                //
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


//                    Intent i = new Intent(acc_home.this,acc_Game.class);
//                    startActivity(i);
                }
            }
        });

    }




        //set header text end
        //sidebar end


//sidebar start

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acc_home){
            Intent intent = new Intent(getBaseContext(),acc_home.class);
            startActivity(intent);

        }else if (id == R.id.acc_profilesetting){
            Intent intent = new Intent(getBaseContext(),acc_profile.class);
            startActivity(intent);
        }else if (id == R.id.acc_logout){
            mAuth.signOut();
            finish();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_Instruction){
            Intent intent = new Intent(getBaseContext(),acc_instruction.class);
            startActivity(intent);
        }
        return false;
    }

    //sidebar end


}
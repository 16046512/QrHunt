package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    //sidebar end

    private EditText mainEtName,mainEtCode;
    private Button main_start;

    //Firebase start
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase mRef;
    //Firebase end

    private String userid,iscreated,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        checkuser();
        //firebase

        //sidebar start
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        //sidebar end

        main_start = (Button)findViewById(R.id.main_start);
        mainEtCode = (EditText)findViewById(R.id.mainEtCode);
        mainEtName = (EditText)findViewById(R.id.mainEtName);


        //get preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final String name = prefs.getString("name","");



        if(!name.equals("")){
            mainEtName.setText(name);
        }

            main_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mainEtCode.getText().toString().equals("") && !mainEtName.getText().toString().equals("")) {

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor prefEdit = prefs.edit();
                        prefEdit.putString("name", mainEtName.getText().toString());
                        prefEdit.putString("code", mainEtCode.getText().toString());
                        prefEdit.commit();


                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + mainEtCode.getText().toString());
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, String> map = dataSnapshot.getValue(Map.class);
                                    final String title = map.get("title");
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


                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + "Guest "+mainEtName.getText().toString());
                                    Log.i("main",""+mRef);
                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot2) {
                                            if (dataSnapshot2.exists()) {


                                               Toast.makeText(MainActivity.this, "Name has been used", Toast.LENGTH_LONG).show();

                                            } else {
                                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/"+title);
                                                Firebase childRef = mRef.child("Guest "+mainEtName.getText().toString());
                                                childRef.child("Name").setValue("Guest "+mainEtName.getText().toString());
                                                childRef.child("QR Found").setValue("0");
                                                childRef.child("Question Correct").setValue("0");
                                                childRef.child("NumOfQuestion").setValue("0/");
                                                childRef.child("Score").setValue("0");
                                                Intent i = new Intent(MainActivity.this, acc_Game.class);
                                                Log.i("main",userid+":"+title+":"+time+":"+mainEtName.getText().toString());
                                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                                SharedPreferences.Editor pe = sp.edit();
                                                pe.putString("guestuserid",userid);
                                                pe.putString("guesttitle",title);
                                                pe.putString("guesttime",time);
                                                pe.putString("guestname","Guest "+mainEtName.getText().toString());
                                                pe.commit();

                                                startActivity(i);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                                } else {
                                    Log.i("MainActivity", "not exist");
                                    Toast.makeText(MainActivity.this, "This code does not exist", Toast.LENGTH_LONG).show();
                                    //
                                }

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    } else if (mainEtName.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "Please enter name to start game", Toast.LENGTH_LONG).show();
                    } else if (mainEtCode.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "Please enter valid code to start game", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter name and code to start game", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    //sidebar start




    @Override
    protected void onResume() {
        super.onResume();
//        Log.i("MainActivity","onresume");

        mAuth.addAuthStateListener(mAuthListener);
        mAuth = FirebaseAuth.getInstance();
        checkuser();

    }
    @Override
    protected void onStart() {
        super.onStart();
//        Log.i("MainActivity","onstart");

        mAuth.addAuthStateListener(mAuthListener);
        mAuth = FirebaseAuth.getInstance();
        checkuser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_Login){
            Intent intentlogin = new Intent(getBaseContext(),nav_login.class);
            startActivity(intentlogin);

        }else if (id == R.id.nav_CreateAccount){
            Intent intent = new Intent(getBaseContext(),nav_create_account.class);
            startActivity(intent);
        }else if (id == R.id.nav_Instruction){
            Intent intent = new Intent(getBaseContext(),nav_instruction.class);
            startActivity(intent);

        }else if (id == R.id.nav_home){
            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);
        }
        return false;
    }

    //sidebar end

    public void checkuser(){
        //firebase start
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //check if user logged in before
//                Log.i("MainActivity","firebaseauth||"+firebaseAuth.getCurrentUser());
                if(firebaseAuth.getCurrentUser()!= null){
                    Intent loginintent = new Intent(MainActivity.this,acc_home.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
            }
        };


        //firebase end
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

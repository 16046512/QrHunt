package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class acc_profile extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView tvwelcome;
    private NavigationView nav_view;
    //sidebar end

    //firebase start
    private TextView tvinstruction;
    private Firebase mRef;
    private FirebaseAuth mAuth;
    //firebase end

    private String oldpass,name,newPass;
    private EditText udEtName,udEtPwd;
    private Button updateprofilebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_profile);

        //firebase start
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        //firebase end

        //bind
        udEtName = (EditText)findViewById(R.id.udEtName);
        udEtPwd = (EditText)findViewById(R.id.udEtPwd);
        updateprofilebtn = (Button)findViewById(R.id.updateprofilebtn);
        //bind end


        //set header text
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);

        nav_view = (NavigationView) findViewById(R.id.navView);
        nav_view.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View header = nav_view.getHeaderView(0);
        tvwelcome = (TextView) header.findViewById(R.id.txtwelcome);
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/"+user.getUid()+"/name");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tvwelcome.setText("Welcome "+value);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });







        final String email = user.getEmail();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(acc_profile.this);
         oldpass = settings.getString("password", "");
         name = settings.getString("name", "");



        updateprofilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!udEtName.getText().equals("")&&!udEtPwd.getText().equals("")) {
                    newPass = udEtPwd.getText().toString();

                    AuthCredential credential = EmailAuthProvider.getCredential(email,oldpass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            Toast.makeText(acc_profile.this, "Something went wrong. Please try again later", Toast.LENGTH_LONG).show();
                                        }else {
                                            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/");
                                            Firebase childRef = mRef.child(user.getUid());
                                            childRef.child("name").setValue(udEtName.getText().toString());
                                            Toast.makeText(acc_profile.this, "Password Successfully Modified", Toast.LENGTH_LONG).show();

                                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(acc_profile.this);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("password", udEtPwd.getText().toString());
                                            editor.putString("name", udEtName.getText().toString());
                                            editor.commit();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(acc_profile.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(acc_profile.this);
        name = settings.getString("name", "");
        udEtName.setText(name);
    }


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
            Intent intentlogin = new Intent(getBaseContext(),acc_home.class);
            startActivity(intentlogin);

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

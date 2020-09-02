package com.example.a16046512.qrhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class nav_create_account extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    //sidebar end


    //firebase start
    private EditText regname,regemail,regpwd;
    private Button regbtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    //firebase end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_create_account);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        //sidebar start
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        //sidebar end


        //firebase start
        mAuth = FirebaseAuth.getInstance();
        regname = (EditText)findViewById(R.id.regEtName);
        regemail = (EditText)findViewById(R.id.regEtEmail);
        regpwd = (EditText)findViewById(R.id.regEtPwd);
        regbtn = (Button)findViewById(R.id.regSignUp);
        mProgress = new ProgressDialog(this);


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        //firebase end
    }

    //firebase start
    private void register(){

        final String name  = regname.getText().toString().trim();
        final String email = regemail.getText().toString().trim();
        String password = regpwd.getText().toString().trim();
        if(name.isEmpty()){
            regname.setError("Name is required");
            regemail.requestFocus();
            return;
        }
        if(email.isEmpty()){
            regemail.setError("Email is required");
            regemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            regemail.setError("Please enter a valid email");
            regemail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            regpwd.setError("Minimum length of password should be 6");
            regpwd.requestFocus();
            return;
        }
        if(password.isEmpty()){
            regpwd.setError("Email is required");
            regpwd.requestFocus();
            return;
        }
        if(!TextUtils.isEmpty(name)||!TextUtils.isEmpty(email)|!TextUtils.isEmpty(password)) {
            mProgress.setMessage("Signing Up ...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db =  mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("email").setValue(email);
                        Toast.makeText(getApplicationContext(), "Register Sucessfully", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                        sendEmailVerification();
                        Intent intent = new Intent(nav_create_account.this,nav_login.class);
                        startActivity(intent);
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(getApplicationContext(), "User Aready Exist", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }


    private void sendEmailVerification(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Check your Email for verification", Toast.LENGTH_LONG).show();

                        mAuth.getInstance().signOut();
                        Intent intent = new Intent(nav_create_account.this, nav_login.class);
                        startActivity(intent);

                    }
                }
            });
        }
    }
    //firebase end

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
        if (id == R.id.nav_Login){
            Intent intent = new Intent(getBaseContext(),nav_login.class);
            startActivity(intent);
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

}

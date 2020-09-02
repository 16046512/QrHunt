package com.example.a16046512.qrhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class nav_login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    //sidebar end

    //firebase start
    private EditText loginEmail;
    private EditText loginPwd;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //firebase end


    //progress dialog
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_login);
        progressDialog =  new ProgressDialog(nav_login.this);
        //firebase
        mAuth = FirebaseAuth.getInstance();
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
        //bind
        loginBtn = (Button) findViewById(R.id.logLogin);
        loginPwd = (EditText)findViewById(R.id.logEtPwd);
        loginEmail = (EditText)findViewById(R.id.logEtEmail);

        //bind

        //firebase start
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //check if user logged in before
                if(firebaseAuth.getCurrentUser()!= null){
                    Intent loginintent = new Intent(nav_login.this,acc_home.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
            }
        };
        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });

        //firebase end

    }


//    //firebase sign in start
private void startSignIn(){
    // Setting up message in progressDialog.
    progressDialog.setMessage("Please Wait");

    // Showing progressDialog.
    progressDialog.show();

    final String email = loginEmail.getText().toString().trim();
    final String password = loginPwd.getText().toString().trim();

    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

        progressDialog.dismiss();
        loginEmail.setError("Email is required");
        loginPwd.setError("Password is required");
        loginEmail.requestFocus();

    }
    if (email.isEmpty()) {

        progressDialog.dismiss();
        loginEmail.setError("Email is required");
        loginEmail.requestFocus();
        return;
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

        progressDialog.dismiss();
        loginEmail.setError("Please enter a valid email");
        loginEmail.requestFocus();
        return;
    }
    if (password.isEmpty()) {

        progressDialog.dismiss();
        loginPwd.setError("Password is required");
        loginPwd.requestFocus();
        return;
    }
    if (password.length() < 6) {

        progressDialog.dismiss();
        loginPwd.setError("Your password must be at least 6 characters long.");
        loginPwd.requestFocus();
        return;
    } else {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {

                    Log.i("nav_login","tttfail:signInWithEmail:failure", task.getException());

                    Toast.makeText(nav_login.this, "Wrong email or password", Toast.LENGTH_LONG).show();

                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        mAuth.getCurrentUser().reload();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(nav_login.this);
                        SharedPreferences.Editor prefEdit = prefs.edit();
                        prefEdit.putString("login","yes");
                        prefEdit.putString("password",loginPwd.getText().toString());
                        prefEdit.commit();

//                        Toast.makeText(nav_login.this, "sign In Sucess", Toast.LENGTH_LONG).show();

                    } else {
                        while (user == null && !user.isEmailVerified() == false) {
                            mAuth.getCurrentUser().reload();
                        }
                        user.sendEmailVerification();
                        Toast.makeText(nav_login.this, "Please verify your email", Toast.LENGTH_LONG).show();
                        mAuth.getInstance().signOut();
                        Intent intent = new Intent(nav_login.this, nav_login.class);
                        startActivity(intent);

                    }

                }
            }
        });
    }

}

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
//    //firebase sign in end




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
//            Toast.makeText(this,"login",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(),nav_login.class);
            startActivity(intent);
        }else if (id == R.id.nav_CreateAccount){
//            Toast.makeText(this,"create account",Toast.LENGTH_LONG).show();
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

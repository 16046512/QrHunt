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
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

public class nav_instruction  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
//sidebar end

    //firebase start
    private TextView tvinstruction;
    private Firebase mRef;
    private Boolean login = false;

    //firebase end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_instruction);


        //sidebar start
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);


        //sidebar end

        tvinstruction = (TextView)findViewById(R.id.navtvinstruction);
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/instruction");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.i("nav_instruction","value:"+value);
                tvinstruction.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Firebase end

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

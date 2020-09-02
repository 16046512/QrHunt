package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class acc_instruction  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_instruction);

        //firebase start
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        //firebase end

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


        //sidebar end

        tvinstruction = (TextView)findViewById(R.id.navtvinstruction);
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/instruction");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.i("acc_instruction",value);
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

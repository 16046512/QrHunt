package com.example.a16046512.qrhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class acc_creategame extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //sidebar start
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView tvwelcome;
    private NavigationView nav_view;
    //sidebar end

    private FirebaseAuth mAuth;
    private Firebase mRef;
    private DatabaseReference mDatabase;

    private Button accCreateNextBtn;
    private Spinner accSNoLocation,accSTime;
    private EditText accEtGameTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_creategame);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");


        accCreateNextBtn = (Button)findViewById(R.id.accCreateNextBtn);

        //firebase start
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
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


        accSNoLocation = (Spinner)findViewById(R.id.accSNoLocation);
        accSTime = (Spinner)findViewById(R.id.accSTime);
        accEtGameTitle = (EditText)findViewById(R.id.accEtGameTitle);


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





        accCreateNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(accEtGameTitle.getText().toString().trim().length() > 0) {

                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/"+user.getUid()+"/Game").child(accEtGameTitle.getText().toString());
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Toast.makeText(acc_creategame.this,"Game has already been exist",Toast.LENGTH_LONG).show();
                            }else{
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/"+user.getUid());
                                Firebase childRef = mRef.child("Game");

                                childRef.child("tmp game title").setValue(accEtGameTitle.getText().toString());
                                childRef.child("tmp location").setValue(accSNoLocation.getSelectedItem().toString());
                                childRef.child("tmp time").setValue(accSTime.getSelectedItem().toString());

                                AlertDialog.Builder myBuilder = new AlertDialog.Builder(acc_creategame.this);
                                myBuilder.setTitle("Please confirm your information");
                                myBuilder.setMessage("Game title:"+accEtGameTitle.getText().toString().trim()+"\nNum location:"+accSNoLocation.getSelectedItem().toString()+"\nTotal time:"+accSTime.getSelectedItem().toString()+" min");
                                myBuilder.setCancelable(true);
                                myBuilder.setPositiveButton("Close",null);
                                myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {


                                        Intent intent = new Intent(getBaseContext(), acc_creategame2.class);
                                        startActivity(intent);
                                        finish();



                                    }
                                });
                                myBuilder.setNeutralButton("Cancel", null);
                                AlertDialog myDialog = myBuilder.create();
                                myDialog.show();
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });




                }else{
                    Toast.makeText(getBaseContext(),"Please enter game title",Toast.LENGTH_LONG);
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
//            Intent intent = new Intent(getBaseContext(),nav_create_account.class);
//            startActivity(intent);
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

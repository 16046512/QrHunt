package com.example.a16046512.qrhunt;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Hint extends AppCompatActivity {

    //Firebase start
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase mRef;
    private String value;
    //Firebase end

    ListView hintlv;
    private String time,guestuserid,guesttitle,guesttime,guestname;

    private ArrayList<String> hintarrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        hintlv = (ListView)findViewById(R.id.hintlv);

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,hintarrayList);
        hintlv.setAdapter(adapter);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        guestuserid = pref.getString("guestuserid","");
        guesttitle = pref.getString("guesttitle","");
        guesttime = pref.getString("guesttime","");
        guestname = pref.getString("guestname","");

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Game/" + guesttitle + "/");
        Log.i("Hint51",mRef+"");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Hint55","in");

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if(!childDataSnapshot.getKey().equals("code")&&!childDataSnapshot.getKey().equals("time")&&!childDataSnapshot.getKey().equals("total location")) {
                        Log.i("Hint55", childDataSnapshot.getKey());
                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + guestuserid + "/Game/" + guesttitle + "/" + childDataSnapshot.getKey() + "/hint");
                        //addValueEventListener
                        Log.i("Hint60", mRef + "");
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                     value = dataSnapshot.getValue(String.class);
                                    Log.i("Hint65", value);
                                    hintarrayList.add(value);
                                    adapter.notifyDataSetChanged();
                                    Log.i("Hint68", hintarrayList+"");
                            }


                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
                Log.i("Hint91",hintarrayList+"");

            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


//


    }
}

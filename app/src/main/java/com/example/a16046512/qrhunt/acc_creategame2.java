
package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class acc_creategame2 extends AppCompatActivity {
    private ListView accLvGame;
    private Button donebtn;
    private FirebaseAuth mAuth;
    private Firebase mRef;
    private String numloc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_creategame2);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        donebtn = (Button) findViewById(R.id.accCreateDoneBtn);
        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                        String location = map.get("tmp location");
                        String gametemptitle = map.get("tmp game title");
                        setNumloc(location);
                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/"+gametemptitle);
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                Log.i("acc_creategame2","count child:"+dataSnapshot.getChildrenCount()+":"+Integer.parseInt(getNumloc()));
                                if(dataSnapshot.getChildrenCount() == Integer.parseInt(getNumloc())+2){
                                    Intent intent = new Intent(acc_creategame2.this, acc_home.class);
                                    startActivity(intent);

                                }else{
                                    Toast.makeText(acc_creategame2.this,"Please enter all question",Toast.LENGTH_LONG).show();
                                }




                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });



                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });






            }
        });



        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                String location = map.get("tmp location");
                Log.i("acc_creategame2","location:"+ location);
                getData(location);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    public void getData(String numloc) {
        ArrayList<String> value = new ArrayList<String>();

        for (int i = 1; i <= Integer.parseInt(numloc.trim()); i++) {
            value.add("Question " + i);
        }
        accLvGame = (ListView) findViewById(R.id.accLvGame);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, value);
        accLvGame.setAdapter(adapter);


        accLvGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //pass data to another page then add the reest of the data
//                Toast.makeText(getBaseContext(), (position + 1) + "", Toast.LENGTH_LONG).show();

                Intent intent3 = new Intent(getBaseContext(), acc_creategame3.class);
                intent3.putExtra("position", position + 1);
                startActivity(intent3);
            }
        });
    }

    public String getNumloc() {
        return numloc;
    }

    public void setNumloc(String numloc) {
        this.numloc = numloc;
    }
}












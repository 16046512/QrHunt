package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class acc_StartGameViewCode extends AppCompatActivity {
    private TextView tvViewCode,tvnoplay,tvgametitle;
    private Firebase mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String selectedgametitle,display;

    private ListView livescorelv;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    Map<String,Integer> namescore =new HashMap<String,Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__start_game_view_code);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        tvViewCode = (TextView)findViewById(R.id.tvViewCode);
        tvnoplay = (TextView)findViewById(R.id.tvnoplay);
        tvgametitle = (TextView)findViewById(R.id.tvgametitle);

        livescorelv = (ListView)findViewById(R.id.livescorelv);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        livescorelv.setAdapter(adapter);


        Intent i = getIntent();
        selectedgametitle = i.getStringExtra("gametitle");
        Log.i("acc_StartGameViewCode","tttgametitle:"+selectedgametitle);

        tvgametitle.setText("Title :"+selectedgametitle);
        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/"+user.getUid()+"/Game/"+selectedgametitle+"/code");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.i("acc_StartGameViewCode","value:"+value);
                tvViewCode.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/"+user.getUid()+"/Player/"+selectedgametitle);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvnoplay.setText("No of player: "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });








        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Player/"+selectedgametitle);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Player/" + selectedgametitle + "/" + childDataSnapshot.getKey()+"/Score");
                    Log.i("accstartgame107",""+mRef);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            Log.i("acc_ViewGame","keyin:"+childDataSnapshot.getKey()+":"+Integer.parseInt(dataSnapshot2.getValue()+""));
//                            Map<String, String> map = dataSnapshot2.getValue(Map.class);
//                            String score = map.get("Score");

                            namescore.put(childDataSnapshot.getKey(),Integer.parseInt(dataSnapshot2.getValue()+""));

//                            adapter.add("Name: "+childDataSnapshot.getKey()+"\nScore: "+dataSnapshot2.getValue());
//                            adapter.notifyDataSetChanged();

                            Map<String, Integer> sortedNameScore = sortByValue(namescore);

                            arrayList.clear();

                            Iterator it = sortedNameScore.entrySet().iterator();
                            int a = 0;
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                arrayList.add("Name: "+pair.getKey() + "\nScore: " + pair.getValue());
                                it.remove(); // avoids a ConcurrentModificationException
                                a++;
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });




                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        Map<String, Integer> sortedNameScore = sortByValue(namescore);
//        Iterator it = sortedNameScore.entrySet().iterator();
//        int a = 0;
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            arrayList.add("Name: "+pair.getKey() + "\nScore: " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//            a++;
//            adapter.notifyDataSetChanged();
//        }

    }


    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }



        return sortedMap;
    }





}

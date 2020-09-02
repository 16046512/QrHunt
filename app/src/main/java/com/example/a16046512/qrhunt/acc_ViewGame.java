package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.ArrayList;
import java.util.Map;

public class acc_ViewGame extends AppCompatActivity {

    ListView gamelistview;
    ArrayAdapter aa;
    ArrayList<Question> question;


    private FirebaseAuth mAuth;
    private Firebase mRef;
    String cate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__view_game);
        gamelistview = (ListView)findViewById(R.id.gamelistview);

//        Log.i("acc_ViewGame","acc_view_game");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(acc_ViewGame.this);
        cate = prefs.getString("cate","");


        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        question = new ArrayList<Question>();



        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/"+cate+"/");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (!childDataSnapshot.getKey().equals("time")&&!childDataSnapshot.getKey().equals("total location")){

//                        Log.i("acc_ViewGame",childDataSnapshot.getKey());
                        if(!childDataSnapshot.getKey().equals("code")) {
                            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + cate + "/" + childDataSnapshot.getKey() + "/");
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    Log.i("acc_ViewGame","keyin:"+childDataSnapshot.getKey());
                                    Map<String, String> map = dataSnapshot2.getValue(Map.class);
                                    String questions = map.get("question");
                                    String ansa = map.get("ansa");
                                    String ansb = map.get("ansb");
                                    String ansc = map.get("ansc");
                                    String ansd = map.get("ansd");
                                    String correntAns = map.get("correctAns");
                                    String hint = map.get("hint");
                                    question.add(new Question(childDataSnapshot.getKey(), questions, ansa, ansb, ansc, ansd, hint, correntAns));
//                                        Log.v("acc_ViewGame", "" + childDataSnapshot.getKey()+questions+ansa+ansb+ansc+ansd+hint+correntAns); //displays the key for the node
//
//
                                    aa.notifyDataSetChanged();


                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }

                    }
                }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        aa = new ViewQuestionAdapter(this,R.layout.acc_view_game_row,question);
        gamelistview.setAdapter(aa);


        gamelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Intent intent3 = new Intent(getBaseContext(), acc_ViewGameEdit.class);
              intent3.putExtra("editgamepos", position + 1);
              Log.i("acc_ViewGame","position"+position+1+"");
              startActivity(intent3);
            }
        });

    }




}

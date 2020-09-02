package com.example.a16046512.qrhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class acc_View extends AppCompatActivity {


    private Firebase mRef;
    private ListView gamelistview;
    private FirebaseAuth mAuth;

    private FirebaseUser user;

    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    String itemValues;
    String listitem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__view);

        Log.i("acc_View","acc_view");
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        gamelistview = (ListView)findViewById(R.id.gamelist);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);

        gamelistview.setAdapter(adapter);

        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (!childDataSnapshot.getKey().equals("tmp game title") && !childDataSnapshot.getKey().equals("tmp location") && !childDataSnapshot.getKey().equals("tmp time")){
                        adapter.add(childDataSnapshot.getKey());
                        adapter.notifyDataSetChanged();
//                        Log.v("acc_View", "" + childDataSnapshot.getKey()); //displays the key for the node

                    }
                }




            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




        gamelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//
                itemValues=(String)gamelistview.getItemAtPosition(position);
                Log.i("acc_View",itemValues);
                Toast.makeText(acc_View.this,itemValues,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(acc_View.this,acc_ViewGame.class);
//                intent.putExtra("cate",itemValues);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(acc_View.this);
                pref.edit().putString("cate",itemValues).commit();
                startActivity(intent);
            }
        });

        registerForContextMenu(gamelistview);

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo adapterContextMenuInformation = (AdapterView.AdapterContextMenuInfo) menuInfo;
        listitem = (String)gamelistview.getItemAtPosition(adapterContextMenuInformation.position);
        menu.setHeaderTitle(listitem);
        menu.add(0,0,0,"Generate Game Code");
        menu.add(0,1,1,"View Code");
        menu.add(0,2,2,"Delete Game");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            Log.i("acc_view",itemValues+":"+listitem);
            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + listitem).child("code");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.i("acc_view","exist");
                        Toast.makeText(acc_View.this,"Game started already...",Toast.LENGTH_LONG).show();
                    }else{
                        Intent intent = new Intent(acc_View.this,acc_StartGame.class);
                        intent.putExtra("gametitle",listitem);
                        startActivity(intent);
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


        }else if(item.getItemId() == 1) {

            Log.i("acc_view",itemValues+":"+listitem);
            mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/" + listitem).child("code");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        Log.i("acc_view","exist");

                        Intent intent = new Intent(acc_View.this,acc_StartGameViewCode.class);
                        intent.putExtra("gametitle",listitem);
                        startActivity(intent);

                    }else{
                        Toast.makeText(acc_View.this,"Please start the game...",Toast.LENGTH_LONG).show();

                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }else if (item.getItemId() == 2) {

            AlertDialog.Builder myBuilder = new AlertDialog.Builder(acc_View.this);
            myBuilder.setTitle("Delete");
            myBuilder.setMessage("Are you sure you want to delete "+listitem);
            myBuilder.setCancelable(false);

            myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + user.getUid() + "/Game/");
                    mRef.child(listitem).removeValue();
                    finish();

                    //to reduct the feel of changing page when reload
                    overridePendingTransition( 0, 0);
                    startActivity(getIntent());
                    overridePendingTransition( 0, 0);

                }
            });
            myBuilder.setNeutralButton("Cancel",null);
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
        }

        return super.onContextItemSelected(item);

    }
}

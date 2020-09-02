package com.example.a16046512.qrhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

public class acc_QRScan extends AppCompatActivity {
    private String qa;
    private TextView tvdoQuestion;
    private Button qra,qrb,qrc,qrd;
    private String splitqa[];

    //firebase start
    private Firebase mRef;

    //firebase end
    private String name,code,userid,title;
    private Boolean exist = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc__qrscan);
        Firebase.setAndroidContext(this);

        //get preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(acc_QRScan.this);
         name = prefs.getString("guestname","");
         code = prefs.getString("code","");
        tvdoQuestion = (TextView)findViewById(R.id.tvdoQuestion);
        qra = (Button)findViewById(R.id.qra);
        qrb = (Button)findViewById(R.id.qrb);
        qrc = (Button)findViewById(R.id.qrc);
        qrd = (Button)findViewById(R.id.qrd);

        new IntentIntegrator(this).initiateScan(); // `this`  is the current Activity



    }
    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {

                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
//                Log.i("accQRscan",result.getContents());
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    qa = result.getContents();
                    splitqa = qa.split("/");
                    tvdoQuestion.setText("Question " + splitqa[0] + " : " + splitqa[1] + "?");
                    qra.setText("A: " + splitqa[2]);
                    qrb.setText("B: " + splitqa[3]);
                    qrc.setText("C: " + splitqa[4]);
                    qrd.setText("D " + splitqa[5]);

                    if (splitqa[7].equals("A")) {

                        qra.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 1;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qra.setTextColor(Color.parseColor("#93f693"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                        qrb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qra.setTextColor(Color.parseColor("#93f693"));
                                                            qrb.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qra.setTextColor(Color.parseColor("#93f693"));
                                                            qrc.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qra.setTextColor(Color.parseColor("#93f693"));
                                                            qrd.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });


                    } else if (splitqa[7].equals("B")) {



                        qra.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrb.setTextColor(Color.parseColor("#93f693"));
                                                            qra.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 1;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrb.setTextColor(Color.parseColor("#93f693"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrb.setTextColor(Color.parseColor("#93f693"));
                                                            qrc.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrb.setTextColor(Color.parseColor("#93f693"));
                                                            qrd.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                    } else if (splitqa[7].equals("C")) {

                        qra.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrc.setTextColor(Color.parseColor("#93f693"));
                                                            qra.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                        qrb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrc.setTextColor(Color.parseColor("#93f693"));
                                                            qrb.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 1;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrc.setTextColor(Color.parseColor("#93f693"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                        qrd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrc.setTextColor(Color.parseColor("#93f693"));
                                                            qrd.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                    } else if (splitqa[7].equals("D")) {

                        qra.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrd.setTextColor(Color.parseColor("#93f693"));
                                                            qra.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                        qrb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrd.setTextColor(Color.parseColor("#93f693"));
                                                            qrb.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                        qrc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 0;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrd.setTextColor(Color.parseColor("#93f693"));
                                                            qrc.setTextColor(Color.parseColor("#F08080"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });

                        qrd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/Game/" + code);
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> map = dataSnapshot.getValue(Map.class);
                                        title = map.get("title");
                                        userid = map.get("userid");

                                        mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/" + name);
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> map = dataSnapshot.getValue(Map.class);

                                                String numofquestion = map.get("NumOfQuestion");
                                                String getname = map.get("Name");
                                                final String qrfound = map.get("QR Found");
                                                String questioncorrect = map.get("Question Correct");

                                                String splitnumofquestion[] = numofquestion.split("/");

                                                for (int i = 0; i < splitnumofquestion.length; i++) {
                                                    Log.i("accQRScan1", splitnumofquestion[i]);
                                                    Log.i("accQRScan2", splitqa[0]);
                                                    if (splitnumofquestion[i].equals(splitqa[0])) {
                                                        exist = true;
                                                        Log.i("accQRScan3", "true");
                                                    } else {
                                                        exist = false;
                                                        Log.i("accQRScan4", "false");
                                                    }
                                                }
                                                if (exist == true) {
                                                    Log.i("accQRScan5", "true");
                                                    Toast.makeText(acc_QRScan.this, "This has been completed before", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.i("accQRScan6", "false");
                                                    final int totalqrfound = Integer.parseInt(qrfound) + 1;
                                                    final int totalquestionCorrect = Integer.parseInt(questioncorrect) + 1;
                                                    final String stringgetid = numofquestion + splitqa[0] + "/";


                                                    mRef = new Firebase("https://qrhunt-f4eab.firebaseio.com/user/" + userid + "/Player/" + title + "/");
                                                    //addValueEventListener
                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Firebase childRef = mRef.child(name);
                                                            childRef.child("QR Found").setValue(Integer.toString(totalqrfound));
                                                            childRef.child("Question Correct").setValue(Integer.toString(totalquestionCorrect));
                                                            childRef.child("NumOfQuestion").setValue(stringgetid);

                                                            qrd.setTextColor(Color.parseColor("#93f693"));
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    finish();
                                                                }
                                                            }, 3000);
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
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

    }


}

package com.example.cangozu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Homepage extends AppCompatActivity {

    static int REQUEST_CODE = 100;
    private TextView data1, data2, data3;
    private FloatingActionButton btn;
    private ArrayList<String> list = new ArrayList<String>();
    private DatabaseHelper db;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        data1 = (TextView) findViewById(R.id.dataFirst);
        data2 = (TextView) findViewById(R.id.dataSecond);
        data3 = (TextView) findViewById(R.id.dataThird);

        btn = (FloatingActionButton) findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homepage.this, Settings.class);
                startActivity(i);
            }
        });
        db = new DatabaseHelper(this);
        Cursor cr = db.getAllData();
        int count = cr.getCount();

        if(count != 0){
            if(cr.moveToFirst()){
                final String name = cr.getString(1);
                textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int result = textToSpeech.setLanguage(Locale.ENGLISH);
                            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Toast.makeText(Homepage.this, "Language is not supported.", Toast.LENGTH_LONG).show();
                            }
                            else{
                                textToSpeech.setPitch(0.6f);
                                textToSpeech.setSpeechRate(1.0f);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    speak("Welcome " + name);
                                }
                            }
                        }
                    }
                });
            }
        }

        else{
            textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        int result = textToSpeech.setLanguage(Locale.ENGLISH);
                        if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                            Toast.makeText(Homepage.this, "Language is not supported.", Toast.LENGTH_LONG).show();
                        }
                        else{
                            textToSpeech.setPitch(0.6f);
                            textToSpeech.setSpeechRate(1.0f);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                speak("Welcome");
                            }
                        }
                    }
                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatabaseReference myRef;
                    FirebaseApp.initializeApp(Homepage.this);

                    myRef =  FirebaseDatabase.getInstance().getReference().child("datas");
                    myRef.addValueEventListener(new ValueEventListener() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override

                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String data1String = dataSnapshot.child("1").getValue().toString();

                            try{
                                Float number = Float.valueOf(data1String);
                                if(number >= 100){
                                    data1String = data1String.charAt(0) + " meter " + data1String.charAt(1) + data1String.charAt(3);
                                }
                                else{
                                    data1String = data1String + "centimeter";
                                }
                            }
                            catch (Exception ex){
                                Toast.makeText(Homepage.this, ex.toString(), Toast.LENGTH_SHORT).show();
                            }
                            speak(data1String);
                            list.add(data1String);
                            int dataCount = list.size();
                            for(int i=0 ; i<dataCount ; i++){
                                data1.setText(list.get(i));
                                if(i<1){
                                    data2.setText("0");
                                }
                                else{
                                    data2.setText(list.get(i-1));
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                catch (Exception ex){
                    Log.d("error", ex.toString());
                    Toast.makeText(Homepage.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(baglimi()){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Toast.makeText(Homepage.this,"internete baglan.", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean baglimi(){
        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net =  con.getActiveNetworkInfo();
        if(net != null && net.isAvailable() && net.isConnected()){
            return true;
        }
        else
            return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            ArrayList<String> datas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(datas.get(0).equals("ayarlar")){
                Intent i = new Intent(Homepage.this, Settings.class);
                startActivity(i);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speak(String text){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else{
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}

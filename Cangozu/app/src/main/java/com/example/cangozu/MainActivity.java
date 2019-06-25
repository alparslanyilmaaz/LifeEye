package com.example.cangozu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech mTTS;
    Context context = this;
    static int REQUEST_CODE = 100;
    private Handler mHandler;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.settings);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
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
                        Toast.makeText(MainActivity.this,"internete baglan.", Toast.LENGTH_LONG).show();
                    }
                break;
            default:{
                Toast.makeText(MainActivity.this,"internete baglan.", Toast.LENGTH_LONG).show();
            }
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
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

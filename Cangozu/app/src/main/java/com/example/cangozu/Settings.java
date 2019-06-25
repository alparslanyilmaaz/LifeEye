package com.example.cangozu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    EditText name, surname;
    Spinner sex;
    Button confirm;
    static int REQUEST_CODE = 100;
    int pressCount = 0;
    DatabaseHelper db;
    String genderHolder = "", nameHolder = "", surnameHolder = "", choose = "";
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new DatabaseHelper(this);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);

        sex = (Spinner) findViewById(R.id.sex);
        String[] items = new String[]{"Male", "Female", "None"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        sex.setAdapter(adapter);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(Settings.this, "Language is not supported.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        textToSpeech.setPitch(0.6f);
                        textToSpeech.setSpeechRate(1.0f);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speak("Welcome the settings page. In here you can create your name, username, and gender. You have to press volume down button for voice control.");
                        }
                    }
                }
            }
        });
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        genderHolder = "male";
                        break;
                    }
                    case 1:{
                        genderHolder = "female";
                        break;
                    }
                    case 2:{
                        genderHolder = "none";
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirm = (Button) findViewById(R.id.confirmButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cr = db.getAllData();
                int count = cr.getCount();

                if(!name.getText().toString().isEmpty()){
                    nameHolder = name.getText().toString();
                }
                else{
                    nameHolder = "";
                }
                if(!surname.getText().toString().isEmpty()){
                    surnameHolder = surname.getText().toString();
                }
                else{
                    surnameHolder = "";
                }
                if(count == 0){
                    db.insertData(nameHolder, surnameHolder, genderHolder);
                    Toast.makeText(Settings.this, "your data saved.", Toast.LENGTH_LONG).show();
                }
                else{
                    if (cr.moveToFirst()){
                        String id = cr.getString(0);
                        db.updateData(id, nameHolder, surnameHolder, genderHolder);
                        Toast.makeText(Settings.this, "your data updated.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                pressCount++;
                if(baglimi()){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Toast.makeText(Settings.this,"You don't have network connection.", Toast.LENGTH_LONG).show();
                }
            case KeyEvent.KEYCODE_VOLUME_UP:
                textToSpeech.stop();
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
            if(pressCount == 1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    speak("Your name saved " + nameHolder);
                }
                nameHolder = datas.get(0);
                name.setText(nameHolder);
            }
            else if (pressCount == 2){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    speak("Your surname saved as " + surnameHolder);
                }
                surnameHolder = datas.get(0);
                surname.setText(surnameHolder);
            }
            else if(pressCount == 3){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    speak("Your gender saved as " + genderHolder);
                }
                genderHolder = datas.get(0);
                if(genderHolder == "male"){
                    sex.setSelection(0);
                }
                else if(genderHolder == "female"){
                    sex.setSelection(1);
                }
                else{
                    sex.setSelection(2);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    speak("For confirm please say okay. For reset please say reset.");
                }
            }
            else if(pressCount == 4){
                choose = datas.get(0);
                if(choose.equals("confirm")){
                    Cursor cr = db.getAllData();
                    int count = cr.getCount();
                    if(count == 0){
                        db.insertData(nameHolder, surnameHolder, genderHolder);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speak("your data inserted.");
                        }
                    }
                    else{
                        if (cr.moveToFirst()){
                            String id = cr.getString(0);
                            db.updateData(id, nameHolder, surnameHolder, genderHolder);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                speak("your data updated.");
                            }
                        }
                    }
                }
                else{
                    pressCount = 0;
                }
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

    @Override
    protected void onPause() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}

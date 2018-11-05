package com.michalraq.proximitylightapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ServerManager extends AppCompatActivity {
    private static final String PREFERENCES = "myPreferences";
    private static final String SERVER_IP = "ipServer";
    private static final String PORT_NUMBER = "portNumber";
    private static final String IS_SERVICE_STARTED = "isServiceStarted";
    private static final String IS_DATA_CHANGED = "isDataChanged";
    private Button buttonSave,buttonStart,buttonStop ;
    private EditText etIPServer;
    private  EditText etPortNumber;
    private SharedPreferences preferences;
    public static boolean isServiceStarted,isDataChanged;
    public ServerContent server;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE); //nazwa pliku do przechowywania ustawien i tryb

         buttonSave = findViewById(R.id.buttonSave);
         etIPServer = findViewById(R.id.editTextServerIP);
         etPortNumber = findViewById(R.id.editTextPortAddress);
         buttonStart = findViewById(R.id.buttonStartService);
         buttonStop = findViewById(R.id.buttonStopService);

         restoreData();

         initButtonListener();
         initButtonStartListener();
         initButtonStopListener();
         server = new ServerContent();

         etIPServer.addTextChangedListener(settingsTextWatcher);
         etPortNumber.addTextChangedListener(settingsTextWatcher);

    }

    private void initButtonListener(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Toast.makeText(ServerManager.this, "Dane zapisano!", Toast.LENGTH_SHORT).show();
            }
        });
    }  private void initButtonStartListener(){
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isServiceStarted = true;

                Toast.makeText(ServerManager.this, "Usługa włączona!", Toast.LENGTH_SHORT).show();

                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                etIPServer.setEnabled(false);
                etPortNumber.setEnabled(false);

                saveServiceStatus();
            }
        });
    }  private void initButtonStopListener(){
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isServiceStarted = false;

                if(!isDataChanged){
                    buttonStart.setEnabled(true);
                    isDataChanged=false;
                }

                Toast.makeText(ServerManager.this, "Serwis zatrzymany!", Toast.LENGTH_SHORT).show();
                buttonStop.setEnabled(false);
                etIPServer.setEnabled(true);
                etPortNumber.setEnabled(true);
                saveServiceStatus();
            }
        });
    }

   private void saveData(){
       isDataChanged = false;

       SharedPreferences.Editor preferencesEditor = preferences.edit();
    String ipServerToSave = etIPServer.getText().toString().trim();
    String portServerToSave = etPortNumber.getText().toString().trim();

    preferencesEditor.putString(SERVER_IP,ipServerToSave);
    preferencesEditor.putString(PORT_NUMBER,portServerToSave);
    preferencesEditor.putBoolean(IS_DATA_CHANGED,isDataChanged);

    server.setServerIP(ipServerToSave);
    server.setPortNumber(Integer.parseInt(portServerToSave));
    preferencesEditor.commit();


    buttonSave.setEnabled(false);
    buttonStart.setEnabled(true);
    }

    private void saveServiceStatus(){
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(IS_SERVICE_STARTED,isServiceStarted);
        preferencesEditor.putBoolean(IS_DATA_CHANGED,isDataChanged);
        preferencesEditor.commit();
    }

   private void restoreData(){
            String ipServerSaved = preferences.getString(SERVER_IP, "");
            String portServerSaved = preferences.getString(PORT_NUMBER, "");
            isServiceStarted = preferences.getBoolean(IS_SERVICE_STARTED,false);
            isDataChanged = preferences.getBoolean(IS_DATA_CHANGED,false);

            etIPServer.setText(ipServerSaved);
            etPortNumber.setText(portServerSaved);

            if(!ipServerSaved.isEmpty() && !portServerSaved.isEmpty()){
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
            }

            if(isServiceStarted) {
                etIPServer.setEnabled(false);
                etPortNumber.setEnabled(false);
                buttonStop.setEnabled(true);
            }else{
                buttonStop.setEnabled(false);
            }

            if(!isDataChanged && !isServiceStarted){
                buttonStart.setEnabled(true);
            }
   }

    private TextWatcher settingsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String portNumber = etPortNumber.getText().toString().trim();
            String ipServer = etIPServer.getText().toString().trim();

            if(!portNumber.isEmpty() && !ipServer.isEmpty()) {
                buttonSave.setEnabled(true);
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
            }else{
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorError));
                buttonSave.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            buttonStart.setEnabled(false);
            isDataChanged=true;
        }
    };
}

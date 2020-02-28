package com.example.callnonstop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int REQUEST_CALL = 6477;
    int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;

    ArrayList<String> listNum = new ArrayList<>();
    EditText multilineNumText;

    TelephonyManager telephonyManager = null;
    PhoneStateListener listener;

    protected void initElements(){
        multilineNumText = (EditText) findViewById(R.id.numMultiLineText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElements();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!checkPermissionForReadExtertalStorage()){
                    try {
                        requestPermissionForReadExtertalStorage();
                    }catch(Exception e){
                        Log.e("ERREUR MATHIAS", "PERMISSION : " + e.getMessage());
                    }
                }else{
                    Intent intent = new Intent()
                            .setType("*/*")
                            .setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                }
            }
        });


        //On fait les demandes de permissions nécessaires à l'utilisation de l'application -----------------

        //on demande le droit pour lire un fichier texte
        if(!checkPermissionForReadExtertalStorage()){
            try {
                requestPermissionForReadExtertalStorage();
            }catch(Exception e){
                Log.e("ERREUR PEMRISSION", "ExternalStorage : " + e.getMessage());
            }
        }

        //On demande le droit pour appeler
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 9999);
        }

        //On demande les status des appels pour pouvoir raccrocher au bout d'un certain moment
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }else{

            launchListenerCall();

        }

        /* -------------------------------------------------------------------------------------------- */

    }

    public void launchListenerCall(){

        Log.e("LOG MATHIAS", "ADD LISTENER");

        if(telephonyManager == null){

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            // Create a new PhoneStateListener
            listener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    String stateString = "N/A";
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            stateString = "Idle";
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            stateString = "Off Hook";
                            //killCall();
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            stateString = "Ringing";
                            break;
                    }

                    Log.e("LISTEN CHANGED MATHIAS", String.format("\nonCallStateChanged: %s",
                            stateString));
                }
            };

            // Register the listener with the telephony manager
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }

    /*public boolean killCall() {

        try {

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d("LOG PHONE STATE","PhoneStateReceiver **" + ex.toString());
            return false;
        }

        return true;
    }*/

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode == RESULT_OK) {

            Uri selectedTexteURI = data.getData();

            String goodPath = new String();
            if(selectedTexteURI.getPath().indexOf("/document/raw:") != -1){
                goodPath = selectedTexteURI.getPath().replace("/document/raw:/storage/emulated/0", "");
            }

            File sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            File testFile = new File(sdcard, goodPath);

            listNum.clear();

            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = br.readLine()) != null) {
                    listNum.add(line);
                }
                br.close();
            }
            catch (IOException e) {
               Toast.makeText(getApplicationContext(), "Erreur lors de la lecture du fichier", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getApplicationContext(), listNum.size() + " numéro(s) trouvé(s)", Toast.LENGTH_SHORT).show();

            updateMultiLineTextNum();

        }
    }

    public void updateMultiLineTextNum(){

        multilineNumText.setText("");

        for(int i = 0; i < listNum.size(); i++){
            if(i == 0){
                multilineNumText.setText(listNum.get(i));
            }else{
                multilineNumText.setText(multilineNumText.getText() + "\n" + listNum.get(i));
            }
        }

        if(listNum.size() > 0) {
            recursiveCall(0);
        }else{
            multilineNumText.setText("Aucun numéro n'a été trouvé");
        }
    }

    int indexNumCallIfNotPermissioned = -1;
    public void recursiveCall(int indexNumToCall){

        indexNumCallIfNotPermissioned = indexNumToCall;

        //on demande la permission pour passer un appel
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {

            //on demande la permission pour voir les status des appels
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }else {

                launchListenerCall();

                if (indexNumToCall != -1) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", listNum.get(indexNumToCall), null));
                    startActivity(intent);
                }

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recursiveCall(indexNumCallIfNotPermissioned);
            } else {
                Toast.makeText(this, "PERMISSION REFUSEE POUR PASSER DES APPELS", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                recursiveCall(indexNumCallIfNotPermissioned);
            }else{
                Toast.makeText(this, "PERMISSION REFUSEE POUR L'ETAT DES APPELS", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

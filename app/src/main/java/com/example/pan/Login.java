package com.example.pan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    Funciones funciones;
    //String url;
    Button enviar;
    EditText user,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        enviar=findViewById(R.id.enviar);
        user=findViewById(R.id.user);
        pass=findViewById(R.id.pass);
        funciones= new Funciones(getApplicationContext());
        if(funciones.Check_Log()){
            startActivity(new Intent(getApplicationContext(),Home.class));
        }
        //Permite la conexion a internet de la activity
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Actualiza la info de la base de datos
        funciones.DescargaInfo();


        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Select_vibrar(getApplicationContext());
                if(PedirPermiso()){

                    funciones.Ingresar(user.getText().toString(),pass.getText().toString());
                }


            }
        });



    }

    public Boolean PedirPermiso() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE};
            int accessFinePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int accessCoarseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int readPhoneState = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);



        /*int readMsn = checkSelfPermission(Manifest.permission.READ_SMS);
        int readMsn2 = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
        int readContacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        int readCalls = checkSelfPermission(Manifest.permission.READ_CALL_LOG);*/

            if (readPhoneState == PackageManager.PERMISSION_GRANTED && accessCoarseLocation == PackageManager.PERMISSION_GRANTED && accessFinePermission == PackageManager.PERMISSION_GRANTED ) {

                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }else{
        }
        return true;
    }

}

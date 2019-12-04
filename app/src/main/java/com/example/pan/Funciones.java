package com.example.pan;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pan.Encuestas.Lista;
import com.example.pan.Servicios.Location;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Funciones {

    Context context;
    Base base ;
    SQLiteDatabase db ;

    public Funciones(Context context) {
        this.context=context;
        base = new Base(context);
        db = base.getWritableDatabase();
    }

    public String Conexion(String data, String url) {

        String result = "";


        try {

            //v.vibrate(50);

            Log.i("Conexion", "Enviando: "+url+"    "+data);

            //Create a URL object holding our url
            URL myUrl = new URL(url);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod("POST");
            connection.setReadTimeout(1500);
            connection.setConnectTimeout(1500);
            //connection.addRequestProperty("pr","33");

            //Connect to our url
            connection.connect();


            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //Log.i("datos",getPostDataString(postDataParams));
            writer.write("data="+data);

            writer.flush();
            writer.close();
            os.close();


            //Create a new InputStreamReader

            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //stringBuilder.append(data);

            //Check if the line we are reading is not null
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
            Log.i("Conexion","Recibiendo: "+result);
            return result;


        } catch (Exception ee) {
            Log.i("Conexion", "Error_conexion: "+ ee.getMessage());
            return  "";
        }



    }



    public String decode64(String str){
        byte[] data = Base64.decode(str, Base64.DEFAULT);
        try {
            str = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String encode64(String toString) {
        byte[] data;
        try {
            data = toString.getBytes("UTF-8");
            toString = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return toString;
    }



    public void Select_vibrar(Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milisegundos
        long[] pattern = { 0, 70};
        v.vibrate(pattern,-1);
    }

    public void Vibrar(int milli){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milisegundos
        long[] pattern = { 0, milli};
        v.vibrate(pattern,-1);
    }

    public void DescargaInfo(){
        String url=URL_Dominio()+context.getString(R.string.url_usuarios);

        String respuesta="";


        try {
            respuesta=Conexion("",url);

            if (respuesta.length() == 0) {
                Toast.makeText(context, "Sin resultados!!", Toast.LENGTH_SHORT).show();
            } else {
                db.execSQL("DELETE from usuarios ");

                JSONArray arrayUsuarios=new JSONArray(respuesta);
                for(int i=0;i<arrayUsuarios.length();i++){
                    JSONObject obj_datos_alum=new JSONObject(arrayUsuarios.getString(i));
                    // Insert con ContentValues
                    ContentValues usuarios = new ContentValues();


                    usuarios.put("id_usuario", obj_datos_alum.getString("id_usuario"));
                    usuarios.put("id_superior", obj_datos_alum.getString("id_superior"));
                    usuarios.put("user", obj_datos_alum.getString("user"));
                    usuarios.put("pass", obj_datos_alum.getString("pass"));
                    usuarios.put("nombre", obj_datos_alum.getString("nombre"));
                    usuarios.put("foto", obj_datos_alum.getString("foto"));
                    usuarios.put("activo", obj_datos_alum.getString("activo"));


                    db.insert("usuarios", null, usuarios);
                    Log.i("DescargaInfo","--->"+respuesta);


                }

            }
        }catch (Exception e)
        {
            Log.i("DescargaInfo","2."+e.getMessage());
        }

    }

    public void GuardarLocation(JSONObject jsonObject){

        try {
            ContentValues locations = new ContentValues();
            //locations.put("id_location", jsonObject.getString("id_location"));
            locations.put("json", jsonObject.toString());

            db.insert("locations", null, locations);
            Log.i("Location","Info: "+jsonObject);
            Vibrar(100);

        } catch (Exception e) {
            Log.i("Location","Error: "+e.getMessage());
            e.printStackTrace();
        }


    }


    public void EnviarLocation() {


        //Log.i("Location","Enviando...: ");
        Cursor c =  db.rawQuery("SELECT * from locations where enviado='0' ",null);
        c.moveToFirst();
        int error=0;
        if(c.getCount()>0){

            while(!c.isAfterLast()){
                try {

                    String url=URL_Dominio()+context.getString(R.string.url_CargarLocation);
                    String respuesta =Conexion(c.getString(c.getColumnIndex("json")),url);
                    Log.i("Location",respuesta);
                    JSONObject jsonObject=new JSONObject(respuesta);
                    Log.i("Location","Respuesta Server: "+respuesta);
                    if(jsonObject.get("response").equals("1")){
                        db.execSQL("UPDATE locations SET enviado='1' WHERE id='"+c.getString(c.getColumnIndex("id"))+"' ");
                    }else{
                        error++;
                    }


                } catch (Exception e) {
                    Log.i("Location","Error: "+e.getMessage());
                }

                c.moveToNext();
            }
            if(error==0){
                //Vibrar(context,100);
            }


        }




        //Lista_size++;

        c.close();
    }


    public void Ingresar(String user,String pass) {
        Log.i("verinfo","mostrando...");
        String nombre="";

        String sql="SELECT * from usuarios where user ='"+user+"' and pass='"+pass+"' ";
        Cursor c =  db.rawQuery(sql,null);
        c.moveToFirst();

        //while(!c.isAfterLast()){
        if(c.getCount()>0){
            try{
                //Log.i("login",c.getString(c.getColumnIndex("nombre")));
                ContentValues usuarios = new ContentValues();

                usuarios.put("id_usuario",c.getString(c.getColumnIndex("id_usuario")));
                usuarios.put("id_superior",c.getString(c.getColumnIndex("id_superior")));
                usuarios.put("user",c.getString(c.getColumnIndex("user")));
                usuarios.put("pass",c.getString(c.getColumnIndex("pass")));
                usuarios.put("nombre",c.getString(c.getColumnIndex("nombre")));
                usuarios.put("foto",c.getString(c.getColumnIndex("foto")));
                usuarios.put("activo",c.getString(c.getColumnIndex("activo")));


                db.insert("login", null, usuarios);

                Toast.makeText(context, "Hola: "+c.getString(c.getColumnIndex("nombre")), Toast.LENGTH_SHORT).show();
                Login();


            }catch(Exception e){
                Toast.makeText(context, "Error al guardar inicio de sesión: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("login","Error:"+e.getMessage());
            }
        }else{
            Toast.makeText(context, "Error en los datos.", Toast.LENGTH_SHORT).show();
        }




        c.close();


    }

    public void Login() {
        context.startActivity(new Intent(context.getApplicationContext(),Home.class));
    }


    public void VerInfo() {
        Log.i("verinfo","mostrando...");
        String nombre="";

        Cursor c =  db.rawQuery("SELECT * from usuarios ",null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            nombre=c.getString(c.getColumnIndex("nombre"));
            Log.i("verinfo",nombre);
            c.moveToNext();
        }

        //Lista_size++;

        c.close();
    }

    public String GetDate(){
        return (String) DateFormat.format("yyyy-MM-dd", new Date());
    }
    public String GetTime(){
        return (String) DateFormat.format("HH:mm:ss", new Date());
    }

    public String GetDateTime(){
        return (String) DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date());
    }

    public boolean Check_Log() {
        Log.i("verinfo","mostrando...");
        String nombre="";

        Cursor c =  db.rawQuery("SELECT * from login ",null);
        c.moveToFirst();
        int cont=c.getCount();
        c.close();

       if(cont>0)
       {
           return true;
           //context.startActivity(new Intent(context.getApplicationContext(),Home.class));
       }
       return false;

        //Lista_size++;


    }


    public void LogOut() {


        db.execSQL("DELETE from login ");
        context.stopService(new Intent(context.getApplicationContext(), Location.class));
        context.startActivity(new Intent(context.getApplicationContext(),Login.class));


    }


    public void GetInfo() {
        Log.i("verinfo","mostrando...");
        String nombre="";

        Cursor c =  db.rawQuery("SELECT * from login ",null);
        c.moveToFirst();


        while(!c.isAfterLast()){
            nombre=c.getString(c.getColumnIndex("nombre"));
            Log.i("verinfo",nombre);
            c.moveToNext();
        }

        //Lista_size++;

        c.close();
    }

    public String URL_Dominio(){
        String URL="";

        URL=context.getString(R.string.dominio_pan);


        return URL;
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public void SetInfo(View headerView) {
        ImageView foto=headerView.findViewById(R.id.foto);
        TextView nombre=headerView.findViewById(R.id.nombre);


        Cursor c =  db.rawQuery("SELECT * from login ",null);
        c.moveToFirst();

        nombre.setText(c.getString(c.getColumnIndex("nombre")));
        foto.setImageBitmap(getBitmapFromURL(c.getString(c.getColumnIndex("foto"))));



        c.close();
    }


    public String GetIdUser() {
        String ID="";


        Cursor c =  db.rawQuery("SELECT * from login ",null);
        c.moveToFirst();

        ID= c.getString(c.getColumnIndex("id_usuario"));

        c.close();
        return ID;
    }

    public String GetUIID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void DescargarEncuestas() {

        String url=URL_Dominio()+context.getString(R.string.url_descargar_encuestas);

        String respuesta="";


        try {
            respuesta=Conexion("",url);

            if (respuesta.length() == 0) {
                Toast.makeText(context, "Sin resultados!!", Toast.LENGTH_SHORT).show();
            } else {
                db.execSQL("DELETE from encuestas ");

                JSONArray arrayEncuestas=new JSONArray(respuesta);
                for(int i=0;i<arrayEncuestas.length();i++){

                    JSONObject jsonObject=new JSONObject(arrayEncuestas.get(i).toString());

                    ContentValues encuestas = new ContentValues();
                    encuestas.put("id_encuesta", jsonObject.get("id_encuesta").toString() );
                    encuestas.put("json", arrayEncuestas.get(i).toString() );

                    db.insert("encuestas", null, encuestas);
                    Log.i("Encuestas","--->"+respuesta);

                }

            }
        }catch (Exception e)
        {
            Log.i("Encuestas","2."+e.getMessage());
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void CargarPreguntas(LinearLayout contenedor,String id_encuesta) {
        ArrayList<JSONObject> preguntas=new ArrayList<>();
        ArrayList<String> id_pregunta=new ArrayList<>();


        Cursor c =  db.rawQuery("SELECT * from encuestas where id_encuesta='"+id_encuesta+"' ",null);
        c.moveToFirst();
        Log.i("CrearPreguntas",c.getCount()+"");

        //Agrego la localizacion a todas las encuestas
        JSONObject jsonPreguntas0=new JSONObject();

        try {
            jsonPreguntas0.put("opciones","");
            jsonPreguntas0.put("tipo","0");
            jsonPreguntas0.put("pregunta","Location");
            preguntas.add(jsonPreguntas0);
        } catch (JSONException e) {
            e.printStackTrace();
        }





        while(!c.isAfterLast()){
            //{"fecha":"2019-10-26","titulo":"Encuesta1","id_encuesta":"54449b0054b0478b89516fc5636f9d38","id_pregunta":"6b0e881b9d93494cbcf9453cad8db006","pregunta":"Numero","tipo":"6","extra":"{\"opciones\":[{\"value\":\"1\",\"texto\":\"1\"},{\"value\":\"2\",\"texto\":\"2\"},{\"value\":\"3\",\"texto\":\"3\"}]"}
            try {
                JSONObject jsonObject=new JSONObject(c.getString(c.getColumnIndex("json")));

                JSONObject jsonPreguntas=new JSONObject();

                jsonPreguntas.put("opciones",jsonObject.get("extra").toString());
                jsonPreguntas.put("tipo",jsonObject.get("tipo").toString());
                jsonPreguntas.put("pregunta",jsonObject.get("pregunta").toString());

                preguntas.add(jsonPreguntas);
                Log.i("CrearPreguntas","ArraylistPregunta: "+jsonPreguntas.toString());

                id_pregunta.add(jsonObject.get("id_pregunta").toString());



            } catch (JSONException e) {
                Log.i("CrearPreguntas","Error: "+e.getMessage());
            }


            c.moveToNext();
        }


        c.close();
        GeneraPreguntas(contenedor,id_pregunta,preguntas);

    }

    private void GeneraPreguntas(final LinearLayout contenedor,final ArrayList<String> id_pregunta, ArrayList<JSONObject> preguntas) {
        Button enviar=null;
        TextView texto = null;
        EditText edit = null;
        Spinner spinner = null;
        RadioGroup radioGroup=null;
        RadioButton radioButton=null;
        CheckBox checkBox=null;
        final ArrayList<String> respuesta=new ArrayList<>();


        LinearLayout.LayoutParams parameter = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parameter.setMargins(0,20,0,0); // left, top, right, bottom



        int Id=0;

        for (int i = 0; i<preguntas.size();i++){
            final int pos=i;
            try{

                switch (preguntas.get(i).getString("tipo")){

                    case "0":

                        Log.i("CrearPreguntas",preguntas.get(i).getString("pregunta"));

                        edit=new EditText(context);
                        edit.setTag(i);
                        edit.setId(Id);

                        edit.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                //EditText edi=contenedor.getRootView().findViewById(v.getId());
                                //Toast.makeText(context, "pos: "+pos+" S:"+s+" start: "+start+" before: "+before+" count: "+count+"", Toast.LENGTH_SHORT).show();

                                if(s.length()!=0){
                                    respuesta.set(pos,"{\"id_pregunta\":\""+id_pregunta.get(pos)+"\",\"id_respuesta\":\""+GetUIID()+"\",\"respuesta\":\""+s+"\"}");
                                }else{
                                    respuesta.set(pos,"");
                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        ObtenerLocation(edit);

                        Id++;
                        contenedor.addView(edit);

                        break;


                    case "1":
                        texto =new TextView(context);
                        texto.setLayoutParams(parameter);
                        texto.setText(preguntas.get(i).getString("pregunta"));
                        contenedor.addView(texto);

                        Log.i("CrearPreguntas",preguntas.get(i).getString("pregunta"));

                        edit=new EditText(context);
                        edit.setTag(i);
                        edit.setId(Id);
                        edit.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                //EditText edi=contenedor.getRootView().findViewById(v.getId());
                                //Toast.makeText(context, "pos: "+pos+" S:"+s+" start: "+start+" before: "+before+" count: "+count+"", Toast.LENGTH_SHORT).show();

                                if(s.length()!=0){
                                    respuesta.set(pos,"{\"id_pregunta\":\""+id_pregunta.get(pos)+"\",\"id_respuesta\":\""+GetUIID()+"\",\"respuesta\":\""+s+"\"}");
                                }else{
                                    respuesta.set(pos,"");
                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });


                        Id++;
                        contenedor.addView(edit);
                        break;
                    case "2":
                        texto =new TextView(context);
                        texto.setLayoutParams(parameter);
                        texto.setText(preguntas.get(i).getString("pregunta"));
                        contenedor.addView(texto);

                        Log.i("CrearPreguntas",preguntas.get(i).getString("pregunta"));


                        spinner =new Spinner(context);

                        ArrayList<Select> select=new ArrayList<>();

                        final JSONArray jsonArray2=new JSONArray(preguntas.get(i).getString("opciones"));

                        select.add(new Select("--Elegir--"));

                        for (int j = 0; j<jsonArray2.length();j++){
                            JSONObject jsonObject=new JSONObject(jsonArray2.getString(j));
                            select.add(new Select(jsonObject.getString("texto")));
                        }
                        ArrayAdapter<Select> comboAdapter = new ArrayAdapter(context, R.layout.spinner_item, select);
                        //Cargo el spinner con los datos
                        spinner.setAdapter(comboAdapter);
                        spinner.setTag(i);
                        spinner.setId(Id);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position!=0){
                                    respuesta.set(pos,"{\"id_pregunta\":\""+id_pregunta.get(pos)+"\",\"id_respuesta\":\""+GetUIID()+"\",\"respuesta\":\""+parent.getSelectedItem()+"\"}");
                                }else{
                                    respuesta.set(pos,"");
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        Id++;
                        contenedor.addView(spinner);

                        break;

                    case "3":
                        texto =new TextView(context);
                        texto.setLayoutParams(parameter);
                        texto.setText(preguntas.get(i).getString("pregunta"));
                        contenedor.addView(texto);

                        Log.i("CrearPreguntas",preguntas.get(i).getString("pregunta"));


                        radioGroup =new RadioGroup(context);

                        //ArrayList<Select> select=new ArrayList<>();

                        JSONArray jsonArray3=new JSONArray(preguntas.get(i).getString("opciones"));

                        //select.add(new Select("0","--Elegir--"));
                        for (int j = 0; j<jsonArray3.length();j++){
                            JSONObject jsonObject=new JSONObject(jsonArray3.getString(j));
                            radioButton=new RadioButton(context);
                            radioButton.setText(jsonObject.getString("texto"));
                            radioButton.setId(Id);
                            radioButton.setTag(i);

                            Id++;
                            radioGroup.addView(radioButton);

                            //select.add(new Select(jsonObject.getString("value"),jsonObject.getString("texto")));
                        }
                        //ArrayAdapter<Select> comboAdapter = new ArrayAdapter(context, R.layout.spinner_item, select);
                        //Cargo el spinner con los datos
                        //spinner.setAdapter(comboAdapter);


                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton radioButton1=contenedor.getRootView().findViewById(group.getCheckedRadioButtonId());

                                respuesta.set(pos,"{\"id_pregunta\":\""+id_pregunta.get(pos)+"\",\"id_respuesta\":\""+GetUIID()+"\",\"respuesta\":\""+radioButton1.getText()+"\"}");

                            }
                        });

                        contenedor.addView(radioGroup);
                    break;

                    case "4":
                        texto =new TextView(context);
                        texto.setLayoutParams(parameter);
                        texto.setText(preguntas.get(i).getString("pregunta"));
                        contenedor.addView(texto);

                        Log.i("CrearPreguntas",preguntas.get(i).getString("pregunta"));

                        JSONArray jsonArray4=new JSONArray(preguntas.get(i).getString("opciones"));

                        //select.add(new Select("0","--Elegir--"));
                        final JSONArray jsonArrayt=new JSONArray();
                        for (int j = 0; j<jsonArray4.length();j++){
                            JSONObject jsonObject=new JSONObject(jsonArray4.getString(j));
                            checkBox=new CheckBox(context);
                            checkBox.setText(jsonObject.getString("texto"));
                            checkBox.setId(Id);
                            checkBox.setTag(i);


                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                                    try{
                                        if(isChecked)
                                        {
                                            jsonArrayt.put("{\"texto\":\""+buttonView.getText()+"\"}");
                                        }else{
                                            Quitar(jsonArrayt,"{\"texto\":\""+buttonView.getText()+"\"}");
                                        }

                                        if(jsonArrayt.length()!=0){
                                            respuesta.set(pos,"{\"id_pregunta\":\""+id_pregunta.get(pos)+"\",\"id_respuesta\":\""+GetUIID()+"\",\"respuesta\":"+jsonArrayt+"}");
                                        }else{
                                            respuesta.set(pos,"");
                                        }


                                    }catch (Exception s){
                                        Log.i("CrearPreguntas","Error: "+s.getMessage());
                                    }


                                }
                            });

                            Id++;
                            contenedor.addView(checkBox);

                            //select.add(new Select(jsonObject.getString("value"),jsonObject.getString("texto")));
                        }


                        break;
                }


            }catch (Exception e){
                Log.i("CrearPreguntas","Error: "+e.getMessage());
            }

            respuesta.add(i,""+i);
            //elemento++;
        }



        enviar=new Button(context);
        LinearLayout.LayoutParams  button_param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button_param.setMargins(0,50,0,0);
        enviar.setLayoutParams(button_param);
        enviar.setText("Enviar");
        enviar.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enviar.setBackground(context.getDrawable(R.drawable.botones_azul));
        }

        final TextView repuestas=new TextView(context);
        contenedor.addView(repuestas);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrar(70);

                //Toast.makeText(context, ""+edi.getText(), Toast.LENGTH_SHORT).show();
                String data="";

                for (int i=0 ; i< respuesta.size();i++){
                    data+="\n\n\n\n"+respuesta.get(i);
                }
                repuestas.setText(data);

            }
        });

        contenedor.addView(enviar);
    }

    LocationManager locationManager;
    LocationListener locationListener;

    private void ObtenerLocation(final EditText edit) {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(android.location.Location location) {

                //lon.add(location.getLongitude());
                edit.setText("{\"lat\":\""+location.getLatitude()+"\",\"lon\":\""+location.getLongitude()+"\"}");
                Detener();


            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }else{
            boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (gps_enabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }else if (network_enabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }


        }


    }

    public void Detener(){
        locationManager.removeUpdates(locationListener);
    }

    //Quita objetos json basado en el texto
    public void Quitar(JSONArray jsonArrayt, String s) {

        for(int i = 0; i<jsonArrayt.length();i++){
            try {
                if (jsonArrayt.get(i).equals(s)){
                    jsonArrayt.remove(i);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    class Select {
        private String text;



        public Select( String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }

}

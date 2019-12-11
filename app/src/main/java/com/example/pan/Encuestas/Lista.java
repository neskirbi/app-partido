package com.example.pan.Encuestas;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pan.Base;
import com.example.pan.Funciones;
import com.example.pan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Lista extends AppCompatActivity {

    ArrayList<Encuestas> encuestas=new ArrayList<Encuestas>();
    ArrayList<String> id_encuesta=new ArrayList<String>();
    AdaptadorEncuestas adaptador;
    ListView lista;
    FloatingActionButton refresh;
    Funciones funciones;

    Base base ;
    SQLiteDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        funciones=new Funciones(getApplicationContext());
        base = new Base(getApplicationContext());
        db = base.getWritableDatabase();
        lista=findViewById(R.id.lista);
        refresh=findViewById(R.id.refresh);



        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(70);
                funciones.DescargarEncuestas();
                Llenar();
            }
        });


        adaptador = new AdaptadorEncuestas(getApplicationContext());

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(),Preguntas.class);
                intent.putExtra("id_encuesta",id_encuesta.get(position));
                intent.putExtra("titulo",encuestas.get(position).getTitulo());
                startActivity(intent);
            }
        });

        Llenar();

    }

    void Llenar(){

        encuestas.clear();
        id_encuesta.clear();

        Cursor c =  db.rawQuery("SELECT * from encuestas ",null);
        c.moveToFirst();
        Log.i("Encuestas",c.getCount()+"");

        while(!c.isAfterLast()){
            try {
                JSONObject jsonObject=new JSONObject(c.getString(c.getColumnIndex("json")));
                if(!id_encuesta.contains(jsonObject.get("id_encuesta").toString())){
                    Log.i("Encuestas",jsonObject.get("titulo").toString()+"");
                    id_encuesta.add(jsonObject.get("id_encuesta").toString());
                    encuestas.add(new Encuestas(jsonObject.get("titulo").toString(),jsonObject.get("fecha").toString()));
                }

            } catch (JSONException e) {
                Log.i("Encuestas",e.getMessage()+"");
            }
            //encuestas.add(new Encuestas("Titulo","2019-10-24"));
            //nombre=c.getString(c.getColumnIndex("json"));

            c.moveToNext();
        }

        //Lista_size++;

        c.close();
        /*
        encuestas.add(new Encuestas("Titulo","2019-10-24"));
        encuestas.add(new Encuestas("Titulo","2019-10-24"));
        encuestas.add(new Encuestas("Titulo","2019-10-24"));*/
        lista.setAdapter(adaptador);
    }


    public class Encuestas {
        private String titulo;
        private String fecha;

        public Encuestas(String titulo,String fecha) {
            this.titulo=titulo;
            this.fecha=fecha;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getFecha() {
            return fecha;
        }


    }


    class AdaptadorEncuestas extends ArrayAdapter<Encuestas> {

        public AdaptadorEncuestas(Context context) {
            super( context,R.layout.encuestas, encuestas);
        }



        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View item = inflater.inflate(R.layout.encuestas, null);

            TextView textView1 = item.findViewById(R.id.titulo);
            textView1.setText(encuestas.get(position).getTitulo());


            TextView textView2 = item.findViewById(R.id.fecha);
            textView2.setText(encuestas.get(position).getFecha());




            return(item);
        }
    }
}

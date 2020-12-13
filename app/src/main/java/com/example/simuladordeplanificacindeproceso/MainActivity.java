package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }
    //boton metodo para FIFO
    public void fifo(View view){
        Intent i=new Intent(this,IngresoDatosFifo.class);

        startActivity(i);
    }
    //boton metodo para prioridad apropiativo
    public void prioridadCrecienteApropiativo(View view){
        Intent i=new Intent(this,IngresoDatosPrioridadCrecienteApropiativo.class);

        startActivity(i);
    }

    public void prioridadCrecienteNoApropiativo(View view){
        Intent i=new Intent(this,IngresoDatosPrioridadCrecienteNoApropiativo.class);

        startActivity(i);
    }

    public void prioridadDecrecienteApropiativo(View view){
        Intent i=new Intent(this,IngresoDatosPrioridadDecrecienteApropiativo.class);

        startActivity(i);
    }
    public void prioridadDecrecienteNoApropiativo(View view){
        Intent i=new Intent(this,IngresoDatosPrioridadDecrecienteNoApropiativo.class);

        startActivity(i);
    }

    public void RR(View view){
        Intent i=new Intent(this,IngresoDatosRR.class);

        startActivity(i);
    }
    public void SFJ(View view){
        Intent i=new Intent(this, IngresoDatosSJF.class);

        startActivity(i);
    }
    public void SRTF(View view){
        Intent i=new Intent(this,IngresoDatosSRTF.class);

        startActivity(i);
    }

}

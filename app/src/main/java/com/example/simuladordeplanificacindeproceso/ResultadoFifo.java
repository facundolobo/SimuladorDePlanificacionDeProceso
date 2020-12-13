package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Struct;


public class ResultadoFifo extends AppCompatActivity {

    //estructura de un proceso
    public class procesosEstructura {
        int llegada=0;
        int duracion=0;
        int inicio=-1;
        int termino=-1;
        float tiempo_espera=0;
        float tiempo_retorno=0;
        String nombre="";
    }
    private TextView ET_calculos;
    private TextView ET_ejecucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_fifo);

        ET_calculos=(TextView)findViewById(R.id.ET_calculos); //vinculacion con la ventana
        ET_ejecucion=(TextView)findViewById(R.id.ET_ejecucion);
        String listaCalculos;
        String listaEjecucion;


        //============================

        //bloque pasar los datos de la base de datos a vectores

        //====bloque de creacion y vinculacion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase(); //lectura
        procesosEstructura[] proceso= new procesosEstructura[100];
        procesosEstructura tmp=new procesosEstructura(); //para ordenar los vectores en el metodo burbuja

        //====inicializar todos los vectores posicioin sino da error====
        for(int i=0;i<100;i++){
            proceso[i] =new procesosEstructura();
        }
        //===========================================================



        int N = 0;//cantidad de procesos
        int i = 1;



        //=============copiar de la base de datos=====================


        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre,duracion,llegada FROM procesos " , null);
        if (fila.moveToFirst()) {
            do {
                proceso[i].nombre = fila.getString(0);
                proceso[i].duracion = fila.getInt(1);
                proceso[i].llegada = fila.getInt(2);
                i++;
                N++;
            } while (fila.moveToNext());
        }
        for (i=1;i<=N;i++) //creo necesario
        {
            proceso[i].inicio=-1;
        }

        //========metodo de ordenar el vector por ser un FIFO====
        int x,y;
        for(x = 1; x <= N; x++) {
            for(y = 1; y <= N - x; y++) {

                if(proceso[y].llegada > proceso[y + 1].llegada) {
                    tmp = proceso[y];
                    proceso[y] = proceso[y + 1];
                    proceso[y + 1] = tmp;
                }
            }
        }
        //=====================================================

        int Tr = 0;//tiempo real ! osea el tiempo en q estamos parados
        System.out.println ("\n\n ============================INICIO EJECUCION DE PROCESOS============================ \n");
        listaEjecucion="\n\n INICIO EJECUCION DE PROCESOS \n";




        // verificamos que el indice del vector (i)"  que contiene los procesos no sea mayor al numero de procesos(N)"
        i=1;	// reiniciamos el vector de los prcoesos a 1 ya que
        while (i<= N ){

            //printf ("\n duracion del proceso = %d , nombre del prcoeso %s , Tr = %d",proceso[i].duracion, proceso[i].nombre,Tr);

            while ( proceso[i].duracion != 0 ){ //verificamos so el proceso ya termino

                if (proceso[i].llegada <= Tr){ // si la "llegada del proceso" es igual o menor que el proceso que el "tiempo real" ,

                    if (proceso[i].inicio == -1){//con esto guardamos el tiempo en el q inicio el proceso

                        //System.out.println ("\n\tProceso de nombre = %S inicio en t = %i \n",proceso[i].nombre,Tr);
                        System.out.println ("\n\tProceso de nombre ="+proceso[i].nombre+", inicio en t ="+Tr+"\n");
                        listaEjecucion= listaEjecucion+ "\n\tProceso de nombre ="+proceso[i].nombre+", inicio en t ="+Tr+"\n";

                        proceso[i].inicio = Tr ; //guardamos el tiempo para usarlo en futuros calculos


                    }

                    //printf ("\n resto duracion de un proceso");
                    proceso[i].duracion= proceso[i].duracion - 1; // restamos 1 a la duracion del proceso"
                    Tr = Tr + 1; // aumentamos el tiempo real en 1
                }

                else {
                    Tr ++;	// si resulta que el procesos todavia no a llegado , osea el tiempo de "llegada " menor al tiempo "real" , solos se aumenta el tiempor real en 1
                }
            }

            if (proceso[i].duracion == 0){ // si el tiempo de duracion del proceso es igaul a 0 ; se a terminado el proceso

                System.out.println ("\t \n Proceso de nombre = "+proceso[i].nombre+", termino en t="+Tr+"\n");
                listaEjecucion= listaEjecucion+"\t \n Proceso de nombre = "+proceso[i].nombre+", termino en t="+Tr+"\n";
                proceso[i].termino=Tr;
                i=i+1;
            }


        }
        System.out.println ("\n\n ============================FIN EJECUCION DE PROCESOS===============================\n\n");
        listaEjecucion= listaEjecucion +"\n\n FIN EJECUCION DE PROCESOS\n\n";
        //calculamos tiempo de espera y tiempo de retorno //////////////////////////////////////////
        ET_ejecucion.setText(listaEjecucion);



        for (i=1;i<=N;i++)
        {
            proceso[i].tiempo_espera= proceso[i].inicio - 	proceso[i].llegada;
        }

        for (i=1;i<=N;i++)
        {
            proceso[i].tiempo_retorno= proceso[i].termino - proceso[i].llegada;
        }



        float tiempo_espera_total = 0;
        float tiempo_retorno_total = 0;

        float tiempo_espera_promedo = 0;
        float tiempo_retorno_promedio = 0;


        for (i=1;i<=N;i++)
        {
            tiempo_espera_total = tiempo_espera_total + proceso[i].tiempo_espera;
        }

        for (i=1;i<=N;i++)
        {
            tiempo_retorno_total = tiempo_retorno_total + proceso[i].tiempo_retorno;
        }

        tiempo_espera_promedo = tiempo_espera_total/N;
        tiempo_retorno_promedio = tiempo_retorno_total/N;
        System.out.println ("\n\n ============================CALCULOS================================================ \n\n");
        listaCalculos="CALCULOS";
        //mostramos calculos --------------------------------------
        for (i=1;i<=N;i++)
        {
            //System.out.println ("\n\tProceso de nombre = %s, tiene un tiempo de retorno = %.2f \n",proceso[i].nombre , proceso[i].tiempo_retorno);
            System.out.println ("\n\tProceso de nombre ="+proceso[i].nombre+" tiene un tiempo de retorno ="+proceso[i].tiempo_retorno+"\n");
            listaCalculos=listaCalculos+"\n\tProceso de nombre ="+proceso[i].nombre+" tiene un tiempo de retorno ="+proceso[i].tiempo_retorno+"\n";
        }

        for (i=1;i<=N;i++)
        {
            // System.out.println ("\n\tProceso de nombre = %s, tiene un tiempo de espera = %.2f  \n",proceso[i].nombre , proceso[i].tiempo_espera);

            System.out.println ("\n\tProceso de nombre ="+proceso[i].nombre+" tiene un tiempo de espera ="+proceso[i].tiempo_espera+"\n");
            listaCalculos=listaCalculos+"\n\tProceso de nombre ="+proceso[i].nombre+" tiene un tiempo de espera ="+proceso[i].tiempo_espera+"\n";
        }

        //System.out.println ("\n\tTIEMPO DE ESPERA TOTAL = %.2f \n", tiempo_espera_total );
        System.out.println ("\n\tTIEMPO DE ESPERA TOTAL = "+tiempo_espera_total+"\n" );
        listaCalculos=listaCalculos+"\n\tTIEMPO DE ESPERA TOTAL = "+tiempo_espera_total+"\n";

        // System.out.println ("\n\tTIEMPO DE RETORNO TOTAL = %.2f \n", tiempo_retorno_total );
        System.out.println ("\n\tTIEMPO DE RETORNO TOTAL ="+tiempo_retorno_total +"\n");
        listaCalculos=listaCalculos+"\n\tTIEMPO DE RETORNO TOTAL ="+tiempo_retorno_total +"\n";

        // System.out.println ("\n\tTIEMPO DE ESPERA PROMEDIO = %.2f \n", tiempo_espera_promedo );
        System.out.println ("\n\tTIEMPO DE ESPERA PROMEDIO ="+tiempo_espera_promedo+"\n" );
        listaCalculos=listaCalculos+"\n\tTIEMPO DE ESPERA PROMEDIO ="+tiempo_espera_promedo+"\n";

        //System.out.println ("\n\tTIEMPO DE RETORNO PROMEDIO = %.2f \n", tiempo_retorno_promedio );
        System.out.println ("\n\tTIEMPO DE RETORNO PROMEDIO ="+tiempo_retorno_promedio+"\n" );
        listaCalculos=listaCalculos+"\n\tTIEMPO DE RETORNO PROMEDIO ="+tiempo_retorno_promedio+"\n";
        ///////////////////////////////////////////////////////////////////

        for (i=1; i<=N;i++){ //solo muestra los resultados finales
            // System.out.println ("\n\tEl proceso de nombre = %s,  inicio en = %d y termino en = %d \n", proceso[i].nombre , proceso[i].inicio,proceso[i].termino);
            System.out.println ("\n\tEl proceso de nombre = "+proceso[i].nombre+", inicio en ="+proceso[i].inicio+",termino en ="+proceso[i].termino);
            listaCalculos=listaCalculos+"\n\tEl proceso de nombre = "+proceso[i].nombre+", inicio en ="+proceso[i].inicio+",termino en ="+proceso[i].termino;
        }

        System.out.println ("\n\n ============================FIN CALCULOS================================================ \n\n");
        listaCalculos=listaCalculos+"\n\nFIN CALCULOS\n\n";

        ET_calculos.setText(listaCalculos);

    }
}

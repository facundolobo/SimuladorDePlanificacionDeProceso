package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class ResultadoSFJ extends AppCompatActivity {
    //estructura de un proceso
    public class procesosEstructura {
        int llegada = 0;
        int duracion = 0;
        int inicio = -1;
        int termino = -1;
        float tiempo_espera = 0;
        float tiempo_retorno = 0;
        //int prioridad;
        String nombre = "";
    }

    private TextView ET_calculos;
    private TextView ET_ejecucion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_sjf);


        ET_calculos = (TextView) findViewById(R.id.ET_calculos); //vinculacion con la ventana
        ET_ejecucion = (TextView) findViewById(R.id.ET_ejecucion);
        String listaCalculos;
        String listaEjecucion;

        //bloque pasar los datos de la base de datos a vectores

        //====bloque de creacion y vinculacion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase(); //lectura
        ResultadoSFJ.procesosEstructura[] proceso = new ResultadoSFJ.procesosEstructura[100];
        ResultadoSFJ.procesosEstructura tmp = new ResultadoSFJ.procesosEstructura(); //para ordenar los vectores en el metodo burbuja

        //====inicializar todos los vectores posicioin sino da error====
        for (int i = 0; i < 100; i++) {
            proceso[i] = new ResultadoSFJ.procesosEstructura();
        }
        //===========================================================
        int i = 1;//indice del vector que contiene los procesos
        int Tr = 0;//tiempo real ! osea el tiempo en q estamos parados
        int N = 0;
        int x, y;


        //=============copiar de la base de datos=====================


        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre,duracion,llegada FROM procesos ", null);
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
        //=======================================================================
//metodo de ordenar el vector por ser un SJF POR DURACION

        for(x = 1; x <= N; x++) {
            for(y = 1; y <= N - x; y++) {

                if(proceso[y].duracion > proceso[y + 1].duracion) {
                    tmp = proceso[y];
                    proceso[y] = proceso[y + 1];
                    proceso[y + 1] = tmp;
                }
            }
        }
        //============================================================
        //metodo de ordenar el vector por ser un SJF POR LLEGADA
        //int x,y;
        for(x = 1; x <= N; x++) {
            for(y = 1; y <= N - x; y++) {

                if(proceso[y].llegada > proceso[y + 1].llegada) {
                    tmp = proceso[y];
                    proceso[y] = proceso[y + 1];
                    proceso[y + 1] = tmp;
                }
            }
        }

        //printf ("\n\n ============================INICIO EJECUCION DE PROCESOS============================ \n");
        listaEjecucion = "\n\n INICIO EJECUCION DE PROCESOS \n";
        // verificamos que el indice del vector (i)"  que contiene los procesos no sea mayor al numero de procesos(N)"
        i=1;	// reiniciamos el vector de los prcoesos a 1 ya que
        while (i<= N ){

            // printf ("\n duracion del proceso = %d , llegada del prcoeso %d , Tr = %d",proceso[i].duracion, proceso[i].llegada,Tr);

            while ( proceso[i].duracion != 0 ){ //verificamos so el proceso ya termino

                if (proceso[i].llegada <= Tr){ // si la "llegada del proceso" es igual o menor que el proceso que el "tiempo real" ,

                    if (proceso[i].inicio == -1){//con esto guardamos el tiempo en el q inicio el proceso

                        //printf ("\n el proceo inicio en Tr =%d",Tr);
                        listaEjecucion=listaEjecucion +"\n\tProceso de nombre = "+proceso[i].nombre+" inicio en t = "+Tr+" \n";
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
                listaEjecucion=listaEjecucion +"\tProceso de nombre = "+proceso[i].nombre+" termino en t = "+Tr+" \n";
                //printf ("\n proceso [%d] termino en t = %d \n",i,Tr);
                proceso[i].termino=Tr;

//============================================================
//metodo de ordenar el vector por ser un SJF POR DURACION

                for(x = 1; x <= N; x++) {
                    for(y = 1; y <= N - x; y++) {

                        if(proceso[y].duracion > proceso[y + 1].duracion) {
                            tmp = proceso[y];
                            proceso[y] = proceso[y + 1];
                            proceso[y + 1] = tmp;
                        }
                    }
                }
                //============================================================



                i=i+1;
            }


        }
        //printf ("\n\n ============================FIN EJECUCION DE PROCESOS===============================\n\n");
        listaEjecucion = listaEjecucion + "\n\n FIN EJECUCION DE PROCESOS\n\n";

        //printf ("\n\n ============================CALCULOS================================================ \n\n");
        listaCalculos = "CALCULOS";
//for (i=1;i<=N;i++){
//printf ("\n\tProceso con llegada en = %d, se ejecuto en = %d y termino en = %d \n", proceso[i].llegada , proceso[i].inicio , proceso[i].termino);

//}
//	printf ("\n procesos terminados");

        //calculamos tiempo de espera y tiempo de retorno //////////////////////////////////////////
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

        //mostramos calculos --------------------------------------
        for (i=1;i<=N;i++)
        {
            listaCalculos=listaCalculos+"\n\tProceso de nombre = "+proceso[i].nombre+", tiene un tiempo de retorno = "+proceso[i].tiempo_retorno+" \n";
        }

        for (i=1;i<=N;i++)
        {
            listaCalculos=listaCalculos+"\n\tProceso de nombre = "+proceso[i].nombre+", tiene un tiempo de espera = "+proceso[i].tiempo_espera+" \n";
        }

        listaCalculos=listaCalculos+"\n\tTIEMPO DE ESPERA TOTAL = "+tiempo_espera_total+" \n";
        listaCalculos=listaCalculos+"\n\tTIEMPO DE RETORNO TOTAL = "+tiempo_retorno_total+" \n" ;

        listaCalculos=listaCalculos+"\n\tTIEMPO DE ESPERA PROMEDIO = "+tiempo_espera_promedo+" \n";
        listaCalculos=listaCalculos+"\n\tTIEMPO DE RETORNO PROMEDIO = "+tiempo_retorno_promedio+" \n";
        ///////////////////////////////////////////////////////////////////

        for (i=1; i<=N;i++){ //solo muestra los resultados finales
            listaCalculos=listaCalculos+"\n\tEl proceso de nombre = "+proceso[i].nombre+",  inicio en = "+proceso[i].inicio+" y termino en = "+proceso[i].termino+" \n";
        }

        ET_ejecucion.setText(listaEjecucion);
        ET_calculos.setText(listaCalculos);
    }
}

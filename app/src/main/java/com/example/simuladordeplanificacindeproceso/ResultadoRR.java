package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class ResultadoRR extends AppCompatActivity {
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
        setContentView(R.layout.activity_resultado_rr);

        ET_calculos = (TextView) findViewById(R.id.ET_calculos); //vinculacion con la ventana
        ET_ejecucion = (TextView) findViewById(R.id.ET_ejecucion);
        String listaCalculos;
        String listaEjecucion;
        //============================

        //bloque pasar los datos de la base de datos a vectores

        //====bloque de creacion y vinculacion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase(); //lectura
        ResultadoRR.procesosEstructura[] proceso = new ResultadoRR.procesosEstructura[100];
        ResultadoRR.procesosEstructura tmp = new ResultadoRR.procesosEstructura(); //para ordenar los vectores en el metodo burbuja

        //====inicializar todos los vectores posicioin sino da error====
        for (int i = 0; i < 100; i++) {
            proceso[i] = new ResultadoRR.procesosEstructura();
        }
        //===========================================================
        int i = 1;//indice del vector que contiene los procesos
        int Tr = 0;//tiempo real ! osea el tiempo en q estamos parados
        int N = 0;
        int x, y;

        int Q=Integer.parseInt(getIntent().getStringExtra("Quantum1"));
        System.out.println("valor 3 Q=" + Q);
        //=============copiar de la base de datos=====================

        System.out.println("copia base de datos");
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

        for (i=1;i<=N;i++)
        {
            proceso[i].tiempo_espera = - proceso[i].llegada;
        }

        //=======================================================================



        //metodo de ordenar el vector por ser un SJF POR LLEGADA
        System.out.println("metodo de ordenar el vector por ser un SJF POR LLEGADA");


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
        //============================================================
        //============================================================

        int banderaQ;
        Tr = proceso[1].llegada;

        int existeproceso = 1;

        //System.out.println ("\n\n ============================INICIO EJECUCION DE PROCESOS============================ \n");
        listaEjecucion = "\n\n INICIO EJECUCION DE PROCESOS \n";
        while (existeproceso == 1)
        {

            for (i=1;i<=N;i++)
            {


                banderaQ = 0;
                while (proceso[i].duracion !=0 && proceso[i].llegada <= Tr)
                {
                    listaEjecucion=listaEjecucion +"\n\tProceso de nombre = "+proceso[i].nombre+" inicio en t = "+Tr+" \n";
                    //System.out.println("while");

                    proceso[i].tiempo_espera = proceso[i].tiempo_espera + Tr; // TIEMPO DE ESPERA---------------------------------------
                    if(proceso[i].inicio == -1) //guardar inicio
                    {
                        //====== GUARDAMOS CUANDO INICIO
                        proceso[i].inicio=Tr;
                        //======
                    }
                    //=============ejecucion
                    proceso[i].duracion -- ;
                    Tr ++;
                    banderaQ ++;

                    proceso[i].tiempo_espera = proceso[i].tiempo_espera - Tr; // TIEMPO DE ESPERA---------------------------------------

                    if (proceso[i].duracion == 0) // guarda termino
                    {
                        //====== GUARDAMOS CUANDO termino
                        proceso[i].termino=Tr;
                        proceso[i].tiempo_espera = proceso[i].tiempo_espera + proceso[i].termino; // TIEMPO DE ESPERA------------------------------------

                        //======
                    }
                    listaEjecucion=listaEjecucion +"\tProceso de nombre = "+proceso[i].nombre+" termino en t = "+Tr+" \n";
                    //salida
                    if (banderaQ == Q)
                    {
                        break; //salimos del bucle

                    }

                }
                //System.out.println("salio de while");
                if (proceso[i+1].llegada > Tr )
                {
                    i=0;
                    Tr++;
                }



            }//fin de for
            //System.out.println("salio de for");
            existeproceso=0;
            for (i=1;i<=N;i++)
            {
                //System.out.println("for ultimo");
                if (proceso[i].duracion !=0)
                {
                    existeproceso=1;
                }

            }
            System.out.println("fin de wwhile");
        }//fin while
        System.out.println ("\n\n ============================FIN EJECUCION DE PROCESOS===============================\n\n");
        listaEjecucion = listaEjecucion + "\n\n FIN EJECUCION DE PROCESOS\n\n";
        System.out.println ("\n\n ============================CALCULOS================================================ \n\n");
        listaCalculos = "CALCULOS";
        //calculamos tiempo de espera y tiempo de retorno //////////////////////////////////////////
// for (i=1;i<=N;i++)
// 	{
// 	proceso[i].tiempo_espera= proceso[i].inicio - 	proceso[i].llegada;
//	 }

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
            listaCalculos=listaCalculos+"\n\tProceso de nombre = "+proceso[i].nombre+", tiene un tiempo de espera = "+proceso[i].tiempo_espera+"  \n";
        }

        listaCalculos=listaCalculos+"\n\tTIEMPO DE ESPERA TOTAL = "+tiempo_espera_total+" \n";
        listaCalculos=listaCalculos+"\n\tTIEMPO DE RETORNO TOTAL = "+tiempo_retorno_total+" \n";

        listaCalculos=listaCalculos+"\n\tTIEMPO DE ESPERA PROMEDIO = "+tiempo_espera_promedo+" \n";
        listaCalculos=listaCalculos+"\n\tTIEMPO DE RETORNO PROMEDIO = "+tiempo_retorno_promedio+" \n";
        ///////////////////////////////////////////////////////////////////

        for (i=1; i<=N;i++){ //solo muestra los resultados finales
            listaCalculos=listaCalculos+"\n\tEl proceso de nombre = "+proceso[i].nombre +",  inicio en = "+proceso[i].inicio+" y termino en = "+proceso[i].termino+" \n";
        }
        System.out.println("termino calculos");
        ET_ejecucion.setText(listaEjecucion);
        ET_calculos.setText(listaCalculos);
    }
}

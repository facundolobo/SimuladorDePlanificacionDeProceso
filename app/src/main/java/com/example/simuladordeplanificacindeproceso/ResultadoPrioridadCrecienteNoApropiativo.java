package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class ResultadoPrioridadCrecienteNoApropiativo extends AppCompatActivity {

    //estructura de un proceso
    public class procesosEstructura {
        int llegada = 0;
        int duracion = 0;
        int inicio = -1;
        int termino = -1;
        float tiempo_espera = 0;
        float tiempo_retorno = 0;
        int prioridad;
        String nombre = "";
    }

    private TextView ET_calculos;
    private TextView ET_ejecucion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_prioridad_creciente_no_apropiativo);

        ET_calculos = (TextView) findViewById(R.id.ET_calculos); //vinculacion con la ventana
        ET_ejecucion = (TextView) findViewById(R.id.ET_ejecucion);
        String listaCalculos;
        String listaEjecucion;

        //============================
        //====bloque de creacion y vinculacion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase(); //lectura
        ResultadoPrioridadCrecienteNoApropiativo.procesosEstructura[] proceso = new ResultadoPrioridadCrecienteNoApropiativo.procesosEstructura[100];
        ResultadoPrioridadCrecienteNoApropiativo.procesosEstructura tmp = new ResultadoPrioridadCrecienteNoApropiativo.procesosEstructura(); //para ordenar los vectores en el metodo burbuja

        //====inicializar todos los vectores posicioin sino da error====
        for (int i = 0; i < 100; i++) {
            proceso[i] = new ResultadoPrioridadCrecienteNoApropiativo.procesosEstructura();
        }
        //===========================================================

        int i = 1;//indice del vector que contiene los procesos
        int Tr = 0;//tiempo real ! osea el tiempo en q estamos parados
        int N = 0;
        int x, y;

        for (i=1;i<=N;i++)
        {
            proceso[i].tiempo_espera = - proceso[i].llegada;
        }
        //=============copiar de la base de datos=====================


        Cursor fila = BaseDeDatos.rawQuery("SELECT nombre,duracion,llegada,prioridad FROM procesos ", null);
        if (fila.moveToFirst()) {
            do {
                proceso[i].nombre = fila.getString(0);
                proceso[i].duracion = fila.getInt(1);
                proceso[i].llegada = fila.getInt(2);
                proceso[i].prioridad = fila.getInt(3);
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

//ordenamos por prioridad

        for(x = 1; x <= N; x++) {
            for(y = 1; y <= N - x; y++) {

                if(proceso[y].prioridad < proceso[y + 1].prioridad) {
                    tmp = proceso[y];
                    proceso[y] = proceso[y + 1];
                    proceso[y + 1] = tmp;
                }
            }
        }
//============================================================




        listaEjecucion = "\n\n INICIO EJECUCION DE PROCESOS \n";

        //printf ("\n\n ============================INICIO EJECUCION DE PROCESOS============================ \n");
        int k,h,bandera=0,existeproceso=1;
        while(existeproceso == 1)
        {
            for (k= 1 ; k <= N ; k++)
            {

                bandera =0;
                if (proceso[k].llegada <= Tr && proceso[k].duracion !=0)
                {
                    //ejecucionn ====
                    //printf ("\n\tProceso de nombre = %s inicio en t = %d \n",proceso[k].nombre,Tr);
                    listaEjecucion=listaEjecucion + "\n\tProceso de nombre =" +proceso[k].nombre + " inicio en t =" +Tr + " \n";

                    proceso[k].tiempo_espera = proceso[k].tiempo_espera + Tr; // TIEMPO DE ESPERA---------------------------------------

                    if(proceso[k].inicio == -1) //guardar inicio
                    {
                        //====== GUARDAMOS CUANDO INICIO
                        proceso[k].inicio=Tr;
                        //======
                    }


                    Tr = Tr + 1;
                    proceso[k].duracion= proceso[k].duracion- 1 ;

                    if (proceso[k].duracion == 0) // guarda termino
                    {
                        //====== GUARDAMOS CUANDO termino
                        proceso[k].termino=Tr;
                        proceso[k].tiempo_espera = proceso[k].tiempo_espera + proceso[k].termino; // TIEMPO DE ESPERA---------------------------------------
                        //======
                    }

                    //printf ("\tProceso de nombre = %s termino en t = %d \n",proceso[k].nombre,Tr);
                    listaEjecucion=listaEjecucion +"\tProceso de nombre = " +proceso[k].nombre + " termino en t = " + Tr+ " \n";


                    proceso[k].tiempo_espera = proceso[k].tiempo_espera - Tr; // TIEMPO DE ESPERA---------------------------------------

                    bandera =1; //bandera para saber si no encopntro un prceso q cumpla

                    break; //salida de aqui si ya ejecuto
                }

            }

            if (bandera ==0)// si no encontro un proceso repasamos todos los precesos apra saber swi todos estan en 0
            {
                existeproceso=0;
                for (i=1;i<=N;i++)
                {
                    if (proceso[i].duracion !=0)
                    {
                        existeproceso=1;
                    }

                }

                if (existeproceso == 1)
                {
                    Tr++;
                }



            }



        }
        //printf ("\n\n ============================FIN EJECUCION DE PROCESOS===============================\n\n");
        //printf ("\n\n ============================FIN EJECUCION DE PROCESOS===============================\n\n");
        listaEjecucion = listaEjecucion + "\n\n FIN EJECUCION DE PROCESOS\n\n";

        //printf ("\n\n ============================CALCULOS================================================ \n\n");
        //printf ("\n\n ============================CALCULOS================================================ \n\n");
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
            //printf ("\n\tProceso de nombre = \" + + \", tiene un tiempo de retorno = \" + + \" \n",proceso[i].nombre , proceso[i].tiempo_retorno);
            listaCalculos = listaCalculos +"\n\tProceso de nombre =" + proceso[i].nombre+ ", tiene un tiempo de retorno =" +proceso[i].tiempo_retorno + " \n";

        }

        for (i=1;i<=N;i++)
        {
            //printf ("\n\tProceso de nombre = \" + + \", tiene un tiempo de espera = \" + + \"  \n",proceso[i].nombre , proceso[i].tiempo_espera);
            listaCalculos = listaCalculos +"\n\tProceso de nombre = " +proceso[i].nombre + ", tiene un tiempo de espera = " + proceso[i].tiempo_espera+ "  \n";

        }

        //printf ("\n\tTIEMPO DE ESPERA TOTAL = \" + + \" \n", tiempo_espera_total );
        listaCalculos = listaCalculos +"\n\tTIEMPO DE ESPERA TOTAL = " + tiempo_espera_total+ " \n";

        //printf ("\n\tTIEMPO DE RETORNO TOTAL = \" + + \" \n", tiempo_retorno_total );
        listaCalculos = listaCalculos +"\n\tTIEMPO DE RETORNO TOTAL = " +tiempo_retorno_total + " \n";


        //printf ("\n\tTIEMPO DE ESPERA PROMEDIO = \" + + \" \n", tiempo_espera_promedo );
        listaCalculos = listaCalculos +"\n\tTIEMPO DE ESPERA PROMEDIO = " +tiempo_espera_promedo + " \n";

        //printf ("\n\tTIEMPO DE RETORNO PROMEDIO = \" + + \" \n", tiempo_retorno_promedio );
        listaCalculos = listaCalculos +"\n\tTIEMPO DE RETORNO PROMEDIO = " +tiempo_retorno_promedio + " \n";

        ///////////////////////////////////////////////////////////////////

        for (i=1; i<=N;i++){ //solo muestra los resultados finales
          //  printf ("\n\tEl proceso de nombre = \" + + \",  inicio en = \" + + \" y termino en = \" + + \" \n", proceso[i].nombre , proceso[i].inicio,proceso[i].termino);
            listaCalculos = listaCalculos +"\n\tEl proceso de nombre = " + proceso[i].nombre+ ",  inicio en = " + proceso[i].inicio+ " y termino en = " + proceso[i].termino+ " \n";

        }
        ET_ejecucion.setText(listaEjecucion);
        ET_calculos.setText(listaCalculos);

    }
}

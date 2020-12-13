package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class  ResultadoPrioridadCrecienteApropiativo extends AppCompatActivity {

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
        setContentView(R.layout.activity_resultado_prioridad_creciente_apropiativo);

        ET_calculos = (TextView) findViewById(R.id.ET_calculos); //vinculacion con la ventana
        ET_ejecucion = (TextView) findViewById(R.id.ET_ejecucion);
        String listaCalculos;
        String listaEjecucion;

        //============================

        //bloque pasar los datos de la base de datos a vectores

        //====bloque de creacion y vinculacion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase(); //lectura
        ResultadoPrioridadCrecienteApropiativo.procesosEstructura[] proceso = new ResultadoPrioridadCrecienteApropiativo.procesosEstructura[100];
        ResultadoPrioridadCrecienteApropiativo.procesosEstructura tmp = new ResultadoPrioridadCrecienteApropiativo.procesosEstructura(); //para ordenar los vectores en el metodo burbuja

        //====inicializar todos los vectores posicioin sino da error====
        for (int i = 0; i < 100; i++) {
            proceso[i] = new ResultadoPrioridadCrecienteApropiativo.procesosEstructura();
        }
        //===========================================================

        int i = 1;//indice del vector que contiene los procesos
        int Tr = 0;//tiempo real ! osea el tiempo en q estamos parados
        int N = 0;
        int x, y;
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

        //=======================================================================
        for (i=1;i<=N;i++) //creo necesario
        {
            proceso[i].inicio=-1;
        }

        //ordenamos por prioridad

        for (x = 1; x <= N; x++) {
            for (y = 1; y <= N - x; y++) {

                if (proceso[y].prioridad < proceso[y + 1].prioridad) { // menor nuero es mayor prioridad
                    tmp = proceso[y];
                    proceso[y] = proceso[y + 1];
                    proceso[y + 1] = tmp;
                }
            }
        }
        //============================================================

        //============================================================
        //metodo de ordenar el vector por POR LLEGADA

        for (x = 1; x <= N; x++) {
            for (y = 1; y <= N - x; y++) {

                if (proceso[y].llegada > proceso[y + 1].llegada) {
                    tmp = proceso[y];
                    proceso[y] = proceso[y + 1];
                    proceso[y + 1] = tmp;
                }
            }
        }
        //============================================================

        listaEjecucion = "\n\n INICIO EJECUCION DE PROCESOS \n";

        //====== GUARDAMOS CUANDO INICIO
    //    Tr = proceso[1].llegada;
    //    proceso[1].inicio = Tr;


        //printf ("\n\tProceso de nombre = %s inicio en t = %d \n",proceso[1].nombre,Tr);
        // listaEjecucion = listaEjecucion + "\n\tProceso de nombre =" + proceso[1].nombre + ", inicio en t =" + Tr + "\n";
//printf ("\n proceso que llega en = %d , inicio en = %d \n",proceso[1].llegada,Tr);

//======

        // Tr = proceso[1].duracion + proceso[1].llegada;
        // proceso[1].duracion = 0;


        //printf ("\tProceso de nombre = %s termino en t = %d \n",proceso[1].nombre,Tr);
        // listaEjecucion = listaEjecucion + "\tProceso de nombre = " + proceso[1].nombre + ", termino en t = " + Tr + "\n";
//printf ("\n proceso que llega en = %d , Termino en = %d \n",proceso[1].llegada,Tr);
//====== GUARDAMOS CUANDO termino
        // proceso[1].termino = Tr;
//======

//=======================================================================
//ordenamos por prioridad

        //   for (x = 1; x <= N; x++) {
        //      for (y = 1; y <= N - x; y++) {

        //         if (proceso[y].prioridad < proceso[y + 1].prioridad) { // menor nuero es mayor prioridad
        //           tmp = proceso[y];
        //           proceso[y] = proceso[y + 1];
        //            proceso[y + 1] = tmp;
        //          }
        //       }
        //   }
        //============================================================

        //nuevo orden
        // for (i=1;i<=N;i++){
// 	printf ("\n NUEVO orden PRIORIDAD de procesos de llegada =%d duracion= %d",proceso[i].llegada,proceso[i].duracion);
        //}

//=============================

// buscamos proceso

        int k,h,salir=0;
        //for (h = 1; h <= N; h++) {
        do{
            for (k = 1; k <= N; k++) {


                if (proceso[k].llegada <= Tr && proceso[k].duracion != 0) {
                    //ejecucionn ====
                    //printf ("\n\tProceso de nombre = %s inicio en t = %d \n",proceso[k].nombre,Tr);
                    listaEjecucion = listaEjecucion + "\n\tProceso de nombre =" + proceso[k].nombre + ", inicio en t =" + Tr + " \n";
                    //printf ("\n proceso que llega en = %d , inicio en = %d \n",proceso[k].llegada,Tr);
                    //====== GUARDAMOS CUANDO INICIO
                    proceso[k].inicio = Tr;
                    //======

                    Tr = Tr + proceso[k].duracion;
                    proceso[k].duracion = 0;

                    //printf ("\tProceso de nombre = %s termino en t = %d \n",proceso[k].nombre,Tr);
                    listaEjecucion = listaEjecucion + "\tProceso de nombre = " + proceso[k].nombre + " termino en t = " + Tr + " \n";
                    //printf (" \n proceso que llega en = %d , termino en = %d \n",proceso[k].llegada,Tr);
                    //====== GUARDAMOS CUANDO termino
                    proceso[k].termino = Tr;
                    //======

                    //         bandera = 1; //bandera para saber si no encopntro un prceso q cumpla

                    break; //salida de aqui si ya ejecuto
                }

            }

            //    if (bandera == 0)// si no encontro un proceso repasamos todos los precesos apra saber si todos estan en 0
            //{
                int existeproceso = 0;
                for (i = 1; i <= N; i++) {
                    if (proceso[i].duracion != 0) {
                        existeproceso = 1;
                    }

                }

                if (existeproceso == 1) {
                    Tr++;
                }else{
                    salir=1;
                }


            //}
        }while(salir !=1 );
        listaEjecucion = listaEjecucion + "\n\n FIN EJECUCION DE PROCESOS\n\n";


        listaCalculos = "CALCULOS";

        //calculamos tiempo de espera y tiempo de retorno //////////////////////////////////////////
        for (i = 1; i <= N; i++) {
            proceso[i].tiempo_espera = proceso[i].inicio - proceso[i].llegada;
        }

        for (i = 1; i <= N; i++) {
            proceso[i].tiempo_retorno = proceso[i].termino - proceso[i].llegada;
        }


        float tiempo_espera_total = 0;
        float tiempo_retorno_total = 0;

        float tiempo_espera_promedo = 0;
        float tiempo_retorno_promedio = 0;


        for (i = 1; i <= N; i++) {
            tiempo_espera_total = tiempo_espera_total + proceso[i].tiempo_espera;
        }

        for (i = 1; i <= N; i++) {
            tiempo_retorno_total = tiempo_retorno_total + proceso[i].tiempo_retorno;
        }

        tiempo_espera_promedo = tiempo_espera_total / N;
        tiempo_retorno_promedio = tiempo_retorno_total / N;

        //mostramos calculos --------------------------------------
        for (i = 1; i <= N; i++) {
            //printf ("\n\tProceso de nombre = %s, tiene un tiempo de retorno = %.2f \n",proceso[i].nombre , proceso[i].tiempo_retorno);
            listaCalculos = listaCalculos + "\n\tProceso de nombre =" + proceso[i].nombre + ", tiene un tiempo de retorno =" + proceso[i].tiempo_retorno + " \n";
        }

        for (i = 1; i <= N; i++) {
            //printf ("\n\tProceso de nombre = %s, tiene un tiempo de espera = %.2f  \n",proceso[i].nombre , proceso[i].tiempo_espera);
            listaCalculos = listaCalculos + "\n\tProceso de nombre =" + proceso[i].nombre + ", tiene un tiempo de espera = " + proceso[i].tiempo_espera + "  \n";
        }

        //printf ("\n\tTIEMPO DE ESPERA TOTAL = %.2f \n", tiempo_espera_total );
        listaCalculos = listaCalculos + "\n\tTIEMPO DE ESPERA TOTAL = " + tiempo_espera_total + " \n";

        //printf ("\n\tTIEMPO DE RETORNO TOTAL = %.2f \n", tiempo_retorno_total );
        listaCalculos = listaCalculos + "\n\tTIEMPO DE RETORNO TOTAL =" + tiempo_retorno_total + " \n";

        //printf ("\n\tTIEMPO DE ESPERA PROMEDIO = %.2f \n", tiempo_espera_promedo );
        listaCalculos = listaCalculos + "\n\tTIEMPO DE ESPERA PROMEDIO =" + tiempo_espera_promedo + " \n";

        //printf ("\n\tTIEMPO DE RETORNO PROMEDIO = %.2f \n", tiempo_retorno_promedio );
        listaCalculos = listaCalculos + "\n\tTIEMPO DE RETORNO PROMEDIO =" + tiempo_retorno_promedio + " \n";
        ///////////////////////////////////////////////////////////////////

        for (i = 1; i <= N; i++) { //solo muestra los resultados finales
            //printf ("\n\tEl proceso de nombre = %s,  inicio en = %d y termino en = %d \n", proceso[i].nombre , proceso[i].inicio,proceso[i].termino);
            listaCalculos = listaCalculos + "\n\tEl proceso de nombre = " + proceso[i].nombre + ",  inicio en =" + proceso[i].inicio + "  y termino en =" + proceso[i].termino + " \n";

        }
        ET_ejecucion.setText(listaEjecucion);
        ET_calculos.setText(listaCalculos);
    }

}
package com.example.simuladordeplanificacindeproceso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class IngresoDatosPrioridadDecrecienteNoApropiativo extends AppCompatActivity {


    private EditText ET_nombre_proceso, ET_llegada, ET_duracion, ET_prioridad;

    private TextView TV_procesos_guardados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_datos_prioridad_creciente_apropiativo);

        //coneccion con los campoos de agregar proceso
        ET_nombre_proceso = (EditText) findViewById(R.id.nombreP);
        ET_duracion = (EditText) findViewById(R.id.duracionP);
        ET_llegada = (EditText) findViewById(R.id.llegadaP);
        ET_prioridad = (EditText) findViewById(R.id.prioridadP);

        //coneccion para los campos de mostrar los procesos

        TV_procesos_guardados = (TextView) findViewById(R.id.todos_proceso);

        //=========coneccion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        //========================

        mostrarProcesosGuardados();

    }

    //funcion mostrar la base de datos=====================================
    public void mostrarProcesosGuardados() {
        //coneccion con la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1); //crear un objeto base de dato
        SQLiteDatabase baseDeDatos = admin.getReadableDatabase(); // la base de datos

        //=========mostrar todos los procesos

        Cursor fila = baseDeDatos.rawQuery("SELECT nombre,duracion,llegada,prioridad FROM procesos", null);

        String listado = "";

        if (fila.moveToFirst()) {
            do {
                listado = listado + "\n ================\n";
                listado = listado + "\n nombre de proceso= " + fila.getString(0) + "\n";
                listado = listado + "\n duracion de proceso= " + fila.getString(1) + "\n";
                listado = listado + "\n llegada de proceso= " + fila.getString(2) + "\n";
                listado = listado + "\n prioridad de proceso= " + fila.getString(3) + "\n";

            } while (fila.moveToNext());
        }

        TV_procesos_guardados.setText(listado);
        //===============================

    }

    //boton metodo agregar proceso a base de datos
    public void RegistrarProceso(View view) {

        //====bloque de creacion y vinculacion con la base de datos

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase(); //escritura
        //============================

        String nombre = ET_nombre_proceso.getText().toString();
        String duracion = ET_duracion.getText().toString();
        String llegada = ET_llegada.getText().toString();
        String prioridad = ET_prioridad.getText().toString();

        if (!nombre.isEmpty() && !duracion.isEmpty() && !llegada.isEmpty() && !prioridad.isEmpty()) {

            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("duracion", duracion);
            registro.put("llegada", llegada);
            registro.put("prioridad", prioridad);

            if (BaseDeDatos.insert("procesos", null, registro) == -1) {

                Toast.makeText(this, "nombre de proceso duplicado", Toast.LENGTH_SHORT).show();
            } else {

                ET_nombre_proceso.setText("");
                ET_duracion.setText("");
                ET_llegada.setText("");
                ET_prioridad.setText("");

                Toast.makeText(this, "registro exitoso", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

        mostrarProcesosGuardados();

        BaseDeDatos.close();


    }

    //boton borrar todos los procesos
    public void borrarTodoProcesos(View view) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        BaseDeDatos.execSQL("DELETE FROM procesos"); //eliminar todos los datos de la tabla

        mostrarProcesosGuardados();

        BaseDeDatos.close();
    }

    //boton eliminar un proceso
    public void eliminarProceso(View view) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

        String codigo = ET_nombre_proceso.getText().toString();

        if (!codigo.isEmpty()) {

            int cantidad = baseDeDatos.delete("procesos", "nombre='" + codigo + "'", null);

            //baseDeDatos.close();
            //ET_nombre_proceso.setText("");
            //ET_llegada.setText("");
            //ET_duracion.setText("");

            if (cantidad == 1) {
                Toast.makeText(this, "proceso eliminado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "el proceso no existe ", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debes llenar el codigo", Toast.LENGTH_SHORT).show();
        }

        mostrarProcesosGuardados();
    }

    // boton calcular
    public void calcular(View view) {

        Intent i = new Intent(this, ResultadoPrioridadDecrecienteNoApropiativo.class);
        startActivity(i);
    }

    // boton agregar prioridad
    public void agregarPrioridadProceso(View view) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "planificacion", null, 1);
        SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

        String nombre_proceso = ET_nombre_proceso.getText().toString();
        String prioridad_nueva = ET_prioridad.getText().toString();

        if (!nombre_proceso.isEmpty()) {

            if (!prioridad_nueva.isEmpty()) {

                //baseDeDatos.execSQL("UPDATE procesos SET prioridad="+prioridad_nueva+" WHERE nombre='"+nombre_proceso+"'");

                //System.out.println("nombre"+nombre_proceso);
                //System.out.println("prioridad"+prioridad_nueva);

                ContentValues registro= new ContentValues();
                registro.put("prioridad",prioridad_nueva);
                int cantidad= baseDeDatos.update("procesos",registro,"nombre='"+nombre_proceso+"'",null);

                if (cantidad==1){
                    Toast.makeText(this, "prioridad agregada/modificada", Toast.LENGTH_SHORT).show();
                    ET_nombre_proceso.setText("");
                    ET_duracion.setText("");
                    ET_llegada.setText("");
                    ET_prioridad.setText("");
                }else{
                    Toast.makeText(this, "no se encontro proceso", Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(this, "Debe ingresar la prioridad", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debe ingresar el nombre del proceso para agregar una prioridad", Toast.LENGTH_SHORT).show();
        }

        mostrarProcesosGuardados();
    }
}

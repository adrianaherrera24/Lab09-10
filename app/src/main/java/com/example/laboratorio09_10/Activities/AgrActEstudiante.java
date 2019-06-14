package com.example.laboratorio09_10.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.laboratorio09_10.Helper.DBHelperCurso;
import com.example.laboratorio09_10.LogicaNegocio.Curso;
import com.example.laboratorio09_10.LogicaNegocio.Estudiante;
import com.example.laboratorio09_10.R;

import java.util.ArrayList;
import java.util.List;

public class AgrActEstudiante extends AppCompatActivity {

    private FloatingActionButton fBtn;
    private boolean editable = true;
    private EditText nomFld;
    private EditText cedFld;
    private EditText edadFld;
    private Spinner cursos;
    private List<Curso> listCursos;
    DBHelperCurso myBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agr_act_estudiante);
        editable = true;
        myBD = new DBHelperCurso(this);

        // button check
        fBtn = findViewById(R.id.addUpdAlumoBtn);

        //cleaning stuff
        nomFld = findViewById(R.id.nombreAddUpdAlumno);
        cedFld = findViewById(R.id.cedulaAddUpdAlumno);
        edadFld=findViewById(R.id.edadAddUpdAlumno);
        nomFld.setText("");
        cedFld.setText("");
        edadFld.setText("");
        cursos = (Spinner) findViewById(R.id.sp_cursos);

        loadCursos();

        //receiving data from admAlumnoActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            editable = extras.getBoolean("editable");
            if (editable) {   // is editing some row
                Estudiante aux = (Estudiante) getIntent().getSerializableExtra("alumno");
                cedFld.setText(aux.getId());
                cedFld.setEnabled(false);
                nomFld.setText(aux.getNombre());
                edadFld.setText(Integer.toString(aux.getEdad()));
                //edit action
                fBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editAlumno();
                    }
                });
            } else {         // is adding new Carrera object
                //add new action
                fBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addAlumno();
                    }
                });
            }
        }
    }

    public void addAlumno() {
        if (validateForm()) {
            //do something
            Curso cur = buscarCurso(ListaCursos(), (Curso)cursos.getSelectedItem());
            Estudiante alumno = new Estudiante(cedFld.getText().toString(), nomFld.getText().toString(),
                    Integer.parseInt(edadFld.getText().toString()), cur);
            Intent intent = new Intent(getBaseContext(), AdmEstudiante.class);
            //sending Alumno data
            intent.putExtra("addAlumno", alumno);
            startActivity(intent);
            finish(); //prevent go back
        }
    }

    public void editAlumno() {
        if (validateForm()) {
            Curso cur = buscarCurso(ListaCursos(), (Curso)cursos.getSelectedItem());
            Estudiante alumno = new Estudiante(cedFld.getText().toString(), nomFld.getText().toString(),
                    Integer.parseInt(edadFld.getText().toString()), cur);
            Intent intent = new Intent(getBaseContext(), AdmEstudiante.class);
            //sending Alumno data
            intent.putExtra("editAlumno", alumno);
            startActivity(intent);
            finish(); //prevent go back
        }
    }

    public boolean validateForm() {
        int error = 0;
        if (TextUtils.isEmpty(this.nomFld.getText())) {
            nomFld.setError("Nombre requerido");
            error++;
        }
        if (TextUtils.isEmpty(this.cedFld.getText())) {
            cedFld.setError("Cedula requerida");
            error++;
        }
        if (error > 0) {
            Toast.makeText(getApplicationContext(), "Algunos errores", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public List<Curso> ListaCursos(){
        Cursor res = myBD.getAllCursos();
        listCursos =new ArrayList<>();
        listCursos.clear();
        if(res.getCount() == 0){
            showMessage("Error","No hay Datos");
        }
        while(res.moveToNext()){
            Curso c = new Curso();
            c.setId(res.getString(0));
            c.setDescripcion(res.getString(1));
            c.setCreditos(Integer.parseInt(res.getString(2)));
            //c.setHoras(Integer.parseInt(res.getString(3)));
            listCursos.add(c);
        }
        //showMessage("Lista de Cursos",L.toString());
        return listCursos;
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public Curso buscarCurso(List<Curso> cursoList, Curso curso){
        for (Curso c : cursoList) {
            if (c.getId().equals(curso.getId())) {
                return c;
            }
        }
        return null;
    }

    private void loadCursos() {
        // im not sure about this
        ArrayAdapter<Curso> adapter = new ArrayAdapter<Curso>(this, R.layout.support_simple_spinner_dropdown_item, ListaCursos());
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        cursos.setAdapter(adapter);
    }

}

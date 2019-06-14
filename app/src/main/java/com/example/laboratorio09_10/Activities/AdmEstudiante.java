package com.example.laboratorio09_10.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.laboratorio09_10.Adapter.EstudianteAdapter;
import com.example.laboratorio09_10.Helper.DBHelperCurso;
import com.example.laboratorio09_10.Helper.RecyclerItemTouchHelper;
import com.example.laboratorio09_10.LogicaNegocio.Curso;
import com.example.laboratorio09_10.LogicaNegocio.Estudiante;
import com.example.laboratorio09_10.MainActivity;
import com.example.laboratorio09_10.R;

import java.util.ArrayList;
import java.util.List;

public class AdmEstudiante extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, EstudianteAdapter.EstudianteAdapterListener{

    private RecyclerView mRecyclerView;
    private EstudianteAdapter mAdapter;
    private List<Estudiante> alumnoList;
    private List<Curso> cursoList;
    private CoordinatorLayout coordinatorLayout;
    private SearchView searchView;
    private FloatingActionButton fab;
    DBHelperCurso myBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_estudiante);
        myBD = new DBHelperCurso(this);

        Toolbar toolbar = findViewById(R.id.toolbar_est);
        setSupportActionBar(toolbar);

        //toolbar fancy stuff
        // getSupportActionBar().setTitle(getString(R.string.my_profesor));

        mRecyclerView = findViewById(R.id.recycler_est);
        alumnoList = new ArrayList<>();
        alumnoList = ListaAlumnos();
        mAdapter = new EstudianteAdapter(alumnoList, this);
        coordinatorLayout = findViewById(R.id.coordinator_est);

        // white background notification bar
        whiteNotificationBar(mRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        // go to update or add career
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddUpdAlumno();
            }
        });

        //delete swiping left and right
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        //should use database info


        // Receive the Carrera sent by AddUpdCarreraActivity
        checkIntentInformation();

        //refresh view
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (direction == ItemTouchHelper.START) {
            if (viewHolder instanceof EstudianteAdapter.MyViewHolder) {
                // get the removed item name to display it in snack bar
                String cedula = alumnoList.get(viewHolder.getAdapterPosition()).getId();
                String name = alumnoList.get(viewHolder.getAdapterPosition()).getNombre();

                myBD.deleteEstudiante(cedula);
                actualiza();

                // save the index deleted
                final int deletedIndex = viewHolder.getAdapterPosition();
                // remove the item from recyclerView
                mAdapter.removeItem(viewHolder.getAdapterPosition());
            }
        } else {
            //If is editing a row object
            Estudiante aux = mAdapter.getSwipedItem(viewHolder.getAdapterPosition());
            //send data to Edit Activity
            Intent intent = new Intent(this, AgrActEstudiante.class);
            intent.putExtra("editable", true);
            intent.putExtra("alumno", aux);
            mAdapter.notifyDataSetChanged(); //restart left swipe view
            startActivity(intent);
        }
    }

    @Override
    public void onItemMove(int source, int target) {
        mAdapter.onItemMove(source, target);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds profesorList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView   !IMPORTANT
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change, every type on input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        Intent a = new Intent(this, MainActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(Estudiante alumno) { //TODO get the select item of recycleView
        Toast.makeText(getApplicationContext(), "Selected: " + alumno.getId() + ", " + alumno.getNombre(), Toast.LENGTH_LONG).show();
    }

    private void checkIntentInformation() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Estudiante aux;
            aux = (Estudiante) getIntent().getSerializableExtra("addAlumno");
            if (aux == null) {
                aux = (Estudiante) getIntent().getSerializableExtra("editAlumno");
                if (aux != null) {
                    editAlumno(aux);
                    actualiza();
                }
            } else {
                addAlumno(aux);
                actualiza();
            }
        }
    }

    private void goToAddUpdAlumno() {
        Intent intent = new Intent(this, AgrActEstudiante.class);
        intent.putExtra("editable", false);
        startActivity(intent);
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public List<Estudiante> ListaAlumnos(){
        Cursor res = myBD.getAllEstudiantes();
        alumnoList.clear();
        if(res.getCount() == 0){
            showMessage("Error","No hay Datos");
        }
        while(res.moveToNext()){
            Estudiante a = new Estudiante();
            a.setId(res.getString(0));
            a.setNombre(res.getString(1));
            //a.setEmail(res.getString(2));
            a.setEdad(Integer.parseInt(res.getString(2)));
            a.setCurso(buscarCurso(ListaCursos(),res.getString(3)));
            alumnoList.add(a);
        }
        //showMessage("Lista de Cursos",L.toString());
        return alumnoList;
    }

    public Curso buscarCurso(List<Curso> cursoList, String codigo){
        for (Curso curso : cursoList) {
            if (curso.getId().equals(codigo)) {
                return curso;
            }
        }
        return null;
    }

    public List<Curso> ListaCursos(){
        Cursor res = myBD.getAllCursos();
        cursoList = new ArrayList<>();
        cursoList.clear();
        if(res.getCount() == 0){
            showMessage("Error","No hay Datos");
        }
        while(res.moveToNext()){
            Curso c = new Curso();
            c.setId(res.getString(0));
            c.setDescripcion(res.getString(1));
            c.setCreditos(Integer.parseInt(res.getString(2)));
            //c.setHoras(Integer.parseInt(res.getString(3)));
            cursoList.add(c);
        }
        //showMessage("Lista de Cursos",L.toString());
        return cursoList;
    }


    public void addAlumno(Estudiante a){

        boolean isInserted = myBD.insertEstudiante(a.getId(),a.getNombre(),a.getEdad(),a.getCurso().getId());
        if(isInserted == true)
            Toast.makeText(AdmEstudiante.this,"Alumno Ingresado",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(AdmEstudiante.this,"Fallo al Ingresar Alumno",Toast.LENGTH_SHORT).show();

    }

    public void editAlumno(Estudiante a) {

        boolean isUpdate = myBD.updateAlumno(a.getId(),a.getNombre(),a.getEdad(),a.getCurso().getId());
        if (isUpdate == true)
            Toast.makeText(AdmEstudiante.this, "Alumno Editado", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(AdmEstudiante.this, "Fallo al Editar", Toast.LENGTH_SHORT).show();

    }

    public void actualiza() {
        List<Estudiante> lista = ListaAlumnos();
        mAdapter = new EstudianteAdapter(lista, AdmEstudiante.this);
        coordinatorLayout = findViewById(R.id.coordinator_est);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(AdmEstudiante.this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

}

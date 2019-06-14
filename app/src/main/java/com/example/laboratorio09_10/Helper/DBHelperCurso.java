package com.example.laboratorio09_10.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelperCurso extends SQLiteOpenHelper {

    public static final String DATBASE_NAME = "Laboratorio9.db";
    public static final String TABLE_NAME_1 = "cursos";
    public static final String TABLE_NAME_2 = "alumnos";

    ///
    public static final String COL1C = "ID";
    public static final String COL2C = "NOMBRE";
    public static final String COL3C = "CREDITOS";

    public static final String COL1E = "ID";
    public static final String COL2E = "NOMBRE";
    public static final String COL3E = "EDAD";
    public static final String COL4E = "CURSO";
    ///

    public DBHelperCurso(Context context) {
        super(context, DATBASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME_1 +" (ID TEXT PRIMARY KEY, NOMBRE TEXT, CREDITOS INTEGER) ");
        db.execSQL("create table " + TABLE_NAME_2 +" (ID TEXT PRIMARY KEY, NOMBRE TEXT, EDAD INTEGER, CURSO TEXT, FOREIGN KEY(CURSO) REFERENCES cursos (ID)) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME_1);
        db.execSQL("drop table if exists "+TABLE_NAME_2);
        onCreate(db);
    }

    public boolean insertCurso(String id,String nombre,int creditos){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1C,id);
        contentValues.put(COL2C,nombre);
        contentValues.put(COL3C,creditos);
        long result = db.insert(TABLE_NAME_1,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME_1, null);
        return res;
    }

    public boolean updateCurso(String codigo,String nombre,int creditos){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1C,codigo);
        contentValues.put(COL2C,nombre);
        contentValues.put(COL3C,creditos);
        db.update(TABLE_NAME_1,contentValues, "ID = ?",new String[]{codigo});
        return  true;
    }

    public Integer deleteCurso(String codigo){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_1, "ID = ?", new String[]{codigo});
    }

    /// alumno
    public boolean insertEstudiante(String id,String nombre, int edad, String curso){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1E,id);
        contentValues.put(COL2E,nombre);
        contentValues.put(COL3E,edad);
        contentValues.put(COL4E,curso);
        long result = db.insert(TABLE_NAME_2,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllEstudiantes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME_2, null);
        return res;
    }

    public Cursor getAllCursos(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from cursos", null);
        return res;
    }

    public boolean updateAlumno(String id,String nombre, int edad, String curso){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1E,id);
        contentValues.put(COL2E,nombre);
        contentValues.put(COL3E,edad);
        contentValues.put(COL4E,curso);
        db.update(TABLE_NAME_2,contentValues, "ID = ?",new String[]{id});
        return  true;
    }

    public Integer deleteEstudiante(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_2, "ID = ?", new String[]{id});
    }
}

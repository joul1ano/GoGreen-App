package com.example.gameapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShownResultsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SelectionLogger.db";

    // SQL statement to create the User table
    private static final String SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE " + ShownResultsContract.ShownResultEntry.TABLE_USER + " (" +
                    ShownResultsContract.ShownResultEntry._ID + " INTEGER PRIMARY KEY," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " TEXT," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_PASSWORD + " TEXT," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_GlASS_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_PAPER_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_ALUMINIUM_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_ELECTRIC_DEVICES_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_BATTERIES_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_CLOTHING_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_OIL_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_PLASTIC_QUANTITY + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_POINTS + " INTEGER)";

    // SQL statement to create the Admin table
    private static final String SQL_CREATE_ADMIN_ENTRIES =
            "CREATE TABLE " + ShownResultsContract.ShownResultEntry.TABLE_ADMIN + " (" +
                    ShownResultsContract.ShownResultEntry._ID + " INTEGER PRIMARY KEY," +
                    ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_USERNAME + " TEXT," +
                    ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_PASSWORD + " TEXT)";

    // SQL statement to create the Application Form table
    private static final String SQL_CREATE_APPLICATION_ENTRIES =
            "CREATE TABLE " + ShownResultsContract.ShownResultEntry.TABLE_APPLICATION_FORM + " (" +
                    ShownResultsContract.ShownResultEntry._ID + " INTEGER PRIMARY KEY," +
                    ShownResultsContract.ShownResultEntry.COLUMN_APPLICANT_USERNAME + " TEXT," +
                    ShownResultsContract.ShownResultEntry.COLUMN_GLASS_ITEMS + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_PAPER_ITEMS + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_ALUMINIUM_ITEMS + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_ELECTRIC_DEVICE_ITEMS + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_BATTERIES + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_CLOTHES + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_USED_OIL_KG + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_PLASTIC_ITEMS + " INTEGER," +
                    ShownResultsContract.ShownResultEntry.COLUMN_APPLICATION_POINTS + " INTEGER)";

    // SQL statement to delete all User entries
    private static final String SQL_DELETE_USER_ENTRIES =
            "DELETE FROM " + ShownResultsContract.ShownResultEntry.TABLE_USER;

    // SQL statement to delete all Admin entries
    private static final String SQL_DELETE_ADMIN_ENTRIES =
            "DELETE FROM " + ShownResultsContract.ShownResultEntry.TABLE_ADMIN;

    // SQL statement to delete all Application Form entries
    private static final String SQL_DELETE_APPLICATION_ENTRIES =
            "DELETE FROM " + ShownResultsContract.ShownResultEntry.TABLE_APPLICATION_FORM;

    // Constructor for the SQLiteOpenHelper
    //
    // Parameters:
    //    context (Context): The application context
    public ShownResultsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the first time
    //
    // Parameters:
    //    sqLiteDatabase (SQLiteDatabase): The database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_USER_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_ADMIN_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_APPLICATION_ENTRIES);
    }

    // Called when the database needs to be upgraded
    //
    // Parameters:
    //    sqLiteDatabase (SQLiteDatabase): The database
    //    i (int): The old database version
    //    i1 (int): The new database version
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_USER_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_ADMIN_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_APPLICATION_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}

package com.sarkerjr.greenBlood.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.sarkerjr.greenBlood.data.BloodContract.DonorEntry;

public class DonorDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "donors.db";

    //Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public DonorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_DONORS_TABLE =  "CREATE TABLE " + DonorEntry.TABLE_NAME + " ("
                + DonorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DonorEntry.COLUMN_DONOR_NAME + " TEXT NOT NULL, "
                + DonorEntry.COLUMN_DONOR_MOBILE + " TEXT NOT NULL, "
                + DonorEntry.COLUMN_DONATE_DATE + " TEXT NOT NULL, "
                + DonorEntry.COLUMN_DONOR_GENDER + " INTEGER NOT NULL, "
                + DonorEntry.COLUMN_BLOOD_GROUP + " INTEGER NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_DONORS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

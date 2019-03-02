package com.sarkerjr.greenBlood.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sarkerjr.greenBlood.data.BloodContract.DonorEntry;

public class DonorProvider extends ContentProvider {

    private static final int DONORS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int DONOR_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(BloodContract.CONTENT_AUTHORITY, BloodContract.PATH_DONORS, DONORS);

        sUriMatcher.addURI(BloodContract.CONTENT_AUTHORITY, BloodContract.PATH_DONORS + "/#", DONOR_ID);
    }

    /** Database helper object */
    private DonorDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DonorDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        cursor = database.query(DonorEntry.TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DONORS:
                return DonorEntry.CONTENT_LIST_TYPE;
            case DONOR_ID:
                return DonorEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DONORS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(DonorEntry.COLUMN_DONOR_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Donor requires a name");
        }

        //Check that donate date is not null
        String donateDate = values.getAsString(DonorEntry.COLUMN_DONATE_DATE);
        if (donateDate == null) {
            throw new IllegalArgumentException("Donor requires to put last donate date");
        }

        //Check that donor mobile is not null
        String mobileNo = values.getAsString(DonorEntry.COLUMN_DONOR_MOBILE);
        if (mobileNo == null) {
            throw new IllegalArgumentException("Donor requires a mobile number");
        }

        // Check that the gender is valid
        Integer gender = values.getAsInteger(DonorEntry.COLUMN_DONOR_GENDER);
        if (gender == null || !DonorEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Donor requires valid gender");
        }

        // Check that donor blood type is valid
        Integer bloodType = values.getAsInteger(DonorEntry.COLUMN_BLOOD_GROUP);
        if (bloodType == null || !DonorEntry.isValidBloodType(bloodType)) {
            throw new IllegalArgumentException("Donor requires valid blood type");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(DonorEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

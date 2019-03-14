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

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case DONORS:
                // For the DONORS code, query the pets table directly with the given
                cursor = database.query(DonorEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DONOR_ID:
                // For the DONOR_ID code, extract out the ID from the URI.
                selection = DonorEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(DonorEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
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

        //Notify to the uri that the cursor has been changed
        getContext().getContentResolver().notifyChange(uri,null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DONORS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DonorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DONOR_ID:
                // Delete a single row given by the ID in the URI
                selection = DonorEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DonorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DONORS:
                return updateDonor(uri, contentValues, selection, selectionArgs);
            case DONOR_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = DonorEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateDonor(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //Update pets in the database with the given content values.
    private int updateDonor(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check that the name value is not null.
        if (values.containsKey(DonorEntry.COLUMN_DONOR_NAME)) {
            String name = values.getAsString(DonorEntry.COLUMN_DONOR_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // check that the mobile No value is not null.
        if (values.containsKey(DonorEntry.COLUMN_DONOR_MOBILE)) {
            String mobile = values.getAsString(DonorEntry.COLUMN_DONOR_MOBILE);
            if (mobile == null) {
                throw new IllegalArgumentException("Donor requires a mobile no");
            }
        }

        // check that the last donate date value is not null.
        if (values.containsKey(DonorEntry.COLUMN_DONATE_DATE)) {
            String lastDonate = values.getAsString(DonorEntry.COLUMN_DONATE_DATE);
            if (lastDonate == null) {
                throw new IllegalArgumentException("Pet requires a last donate date");
            }
        }

        // check that the gender value is valid.
        if (values.containsKey(DonorEntry.COLUMN_DONOR_GENDER)) {
            Integer gender = values.getAsInteger(DonorEntry.COLUMN_DONOR_GENDER);
            if (gender == null || !DonorEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Donor requires valid gender");
            }
        }

        // check that the blood type value is valid.
        if (values.containsKey(DonorEntry.COLUMN_BLOOD_GROUP)) {
            Integer bloodType = values.getAsInteger(DonorEntry.COLUMN_BLOOD_GROUP);
            if (bloodType == null || !DonorEntry.isValidBloodType(bloodType)) {
                throw new IllegalArgumentException("Donor requires valid blood type");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(DonorEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}

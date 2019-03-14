package com.sarkerjr.greenBlood;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import com.sarkerjr.greenBlood.data.BloodContract;
import com.sarkerjr.greenBlood.data.BloodContract.DonorEntry;
import com.sarkerjr.greenBlood.data.DonorCursorAdapter;
import com.sarkerjr.greenBlood.data.DonorProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Use for finding out, if the search button clicking for the first time or repeated
    private boolean searchClick = false;

    //Spinner for selecting blood group on search
    private Spinner mBloodTypeSpinner;

    private int mBloodType;

    private Button searchBtn;

    private static final int DONOR_LOADER = 0;

    DonorCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBtn = (Button) findViewById(R.id.search_btn);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mBloodTypeSpinner = (Spinner) findViewById(R.id.spinner_bloodType_search);

        //Set the blood picker spinner
        setupBloodTypeSpinner();

        // Find the ListView which will be populated with the donor data
        ListView donorListView = (ListView) findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of donor data in the Cursor.
        // There is no donor data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new DonorCursorAdapter(this, null);
        donorListView.setAdapter(mCursorAdapter);

        //Listener for search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the search button click for the first time
                if(searchClick == false){
                    //Kick off the loader in Main_Activity
                    getLoaderManager().initLoader(DONOR_LOADER, null,MainActivity.this);
                    searchClick = true;
                }
                //If the search button pressed repeatedly
                else {
                    //Kick off the loader in Main_Activity
                    getLoaderManager().restartLoader(DONOR_LOADER, null,MainActivity.this);
                }
            }
        });

        donorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to EditorActivity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                Uri currentPetUri = ContentUris.withAppendedId(DonorEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });
    }


    /**Setup spinner for blood type pickup*/

    private void setupBloodTypeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter bloodTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_bloodType_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        bloodTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mBloodTypeSpinner.setAdapter(bloodTypeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mBloodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.a_positive))) {
                        mBloodType = BloodContract.DonorEntry.A_Positive;
                    } else if (selection.equals(getString(R.string.a_negative))) {
                        mBloodType = BloodContract.DonorEntry.A_Negative;
                    } else if (selection.equals(getString(R.string.b_positive))) {
                        mBloodType = BloodContract.DonorEntry.B_Positive;
                    }else if (selection.equals(getString(R.string.b_negative))) {
                        mBloodType = BloodContract.DonorEntry.B_Negative;
                    }else if (selection.equals(getString(R.string.o_positive))) {
                        mBloodType = BloodContract.DonorEntry.O_Positive;
                    }else if (selection.equals(getString(R.string.o_negative))) {
                        mBloodType = BloodContract.DonorEntry.O_Negative;
                    }else if (selection.equals(getString(R.string.ab_positive))) {
                        mBloodType = BloodContract.DonorEntry.AB_Positive;
                    }else if (selection.equals(getString(R.string.ab_negative))) {
                        mBloodType = BloodContract.DonorEntry.AB_Negative;
                    } else{
                        mBloodType = BloodContract.DonorEntry.TYPE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBloodType = BloodContract.DonorEntry.TYPE_UNKNOWN;
            }
        });
    }

    // Get value of readable blood type
    public String getBloodTypeString(int bloodType) {
        switch (bloodType) {
            case DonorEntry.A_Positive:
                return getResources().getString(R.string.a_positive);
            case DonorEntry.A_Negative:
                return getResources().getString(R.string.a_negative);
            case DonorEntry.B_Positive:
                return getResources().getString(R.string.b_positive);
            case DonorEntry.B_Negative:
                return getResources().getString(R.string.b_negative);
            case DonorEntry.O_Positive:
                return getResources().getString(R.string.o_positive);
            case DonorEntry.O_Negative:
                return getResources().getString(R.string.o_negative);
            case DonorEntry.AB_Positive:
                return getResources().getString(R.string.ab_positive);
            case DonorEntry.AB_Negative:
                return getResources().getString(R.string.ab_negative);
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Projection for query method
        String[] projection = {
                DonorEntry._ID,
                DonorEntry.COLUMN_DONOR_NAME,
                DonorEntry.COLUMN_DONOR_MOBILE,
                DonorEntry.COLUMN_DONATE_DATE,
                DonorEntry.COLUMN_BLOOD_GROUP};

        String selection = DonorEntry.COLUMN_BLOOD_GROUP + "=?";

        String[] selectionArgs = new String[]{String.valueOf(mBloodType)};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                DonorEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchClick = false;
    }
}
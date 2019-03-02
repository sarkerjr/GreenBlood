package com.sarkerjr.greenBlood;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Spinner for selecting blood group on search
    private Spinner mBloodTypeSpinner;

    private int mBloodType;

    private Button searchBtn;


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

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatabaseInfo();
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
        bloodTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

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

    private void displayDatabaseInfo(){

        String[] projection = {
                DonorEntry.COLUMN_DONOR_NAME,
                DonorEntry.COLUMN_DONOR_MOBILE,
                DonorEntry.COLUMN_BLOOD_GROUP,
                DonorEntry.COLUMN_DONATE_DATE };

        String selection = DonorEntry.COLUMN_BLOOD_GROUP + "=?";

        String [] selectionArgs = new String[] {getString(mBloodType)};

        Cursor cursor = getContentResolver().query(DonorEntry.CONTENT_URI,
                projection, selection, selectionArgs,null);

        ListView listView = (ListView) findViewById(R.id.list);

        DonorCursorAdapter adapter = new DonorCursorAdapter(this, cursor);

        listView.setAdapter(adapter);
    }
}

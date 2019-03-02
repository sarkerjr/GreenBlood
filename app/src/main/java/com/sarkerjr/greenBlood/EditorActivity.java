package com.sarkerjr.greenBlood;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sarkerjr.greenBlood.data.BloodContract.DonorEntry;

import com.sarkerjr.greenBlood.data.BloodContract;

public class EditorActivity extends AppCompatActivity {

    /** Identifier for the pet data loader */
    private static final int EXISTING_PET_LOADER = 0;

    /** EditText field to enter the donor's name */
    private EditText mNameEditText;

    /** EditText field to enter the donor's breed */
    private EditText mMobileNoEditText;

    /** EditText field to enter the donor's lst blood donate */
    private EditText mLastDonateEditText;

    /** EditText field to enter the donor's gender */
    private Spinner mGenderSpinner;

    /** EditText field to enter the donor's blood type */
    private Spinner mBloodTypeSpinner;

    //For selecting donor gender
    private int mGender = DonorEntry.GENDER_UNKNOWN;

    //For selecting donor blood type
    private int mBloodType = DonorEntry.TYPE_UNKNOWN;

    public Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        mNameEditText = (EditText) findViewById(R.id.edit_donor_name);
        mMobileNoEditText = (EditText) findViewById(R.id.edit_donor_mobile);
        mLastDonateEditText = (EditText) findViewById(R.id.edit_donate_date);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mBloodTypeSpinner = (Spinner) findViewById(R.id.spinner_bloodType);
        mButton = (Button) findViewById(R.id.new_donor_add_button);

        setupGenderSpinner();
        setupBloodTypeSpinner();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDonor();
                finish();
            }
        });
    }

    //Setup spinner for gender pickup
    private void setupGenderSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = DonorEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = DonorEntry.GENDER_FEMALE;
                    } else {
                        mGender = DonorEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = DonorEntry.GENDER_UNKNOWN;
            }
        });
    }

    //Setup spinner for blood type pickup
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
                        mBloodType = DonorEntry.A_Positive;
                    } else if (selection.equals(getString(R.string.a_negative))) {
                        mBloodType = DonorEntry.A_Negative;
                    } else if (selection.equals(getString(R.string.b_positive))) {
                        mBloodType = DonorEntry.B_Positive;
                    }else if (selection.equals(getString(R.string.b_negative))) {
                        mBloodType = DonorEntry.B_Negative;
                    }else if (selection.equals(getString(R.string.o_positive))) {
                        mBloodType = DonorEntry.O_Positive;
                    }else if (selection.equals(getString(R.string.o_negative))) {
                        mBloodType = DonorEntry.O_Negative;
                    }else if (selection.equals(getString(R.string.ab_positive))) {
                        mBloodType = DonorEntry.AB_Positive;
                    }else if (selection.equals(getString(R.string.ab_negative))) {
                        mBloodType = DonorEntry.AB_Negative;
                    } else{
                        mBloodType = DonorEntry.TYPE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBloodType = DonorEntry.TYPE_UNKNOWN;
            }
        });
    }


    /**Method for saving a new donor*/
    private void saveDonor() {
        // Read from input fields
        String nameString = mNameEditText.getText().toString().trim();
        String mobileNoString = mMobileNoEditText.getText().toString().trim();
        String lastDonateString = mLastDonateEditText.getText().toString().trim();

        // Create a ContentValues object using value of EditText field values
        ContentValues values = new ContentValues();
        values.put(DonorEntry.COLUMN_DONOR_NAME, nameString);
        values.put(DonorEntry.COLUMN_DONOR_MOBILE, mobileNoString);
        values.put(DonorEntry.COLUMN_DONATE_DATE, lastDonateString);
        values.put(DonorEntry.COLUMN_DONOR_GENDER, mGender);
        values.put(DonorEntry.COLUMN_BLOOD_GROUP, mBloodType);


        Uri newUri = getContentResolver().insert(DonorEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_donor_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_donor_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

}

package com.sarkerjr.greenBlood;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.sarkerjr.greenBlood.data.BloodContract.DonorEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int EXISTING_DONOR_LOADER = 0;

    /** EditText field to enter the donor's name */
    private EditText mNameEditText;

    /** Content URI for the existing donor (null if it's a new donor) */
    private Uri mCurrentDonorUri;

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

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mDonorHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDonorHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new donor or editing an existing one.
        Intent intent = getIntent();
        mCurrentDonorUri = intent.getData();

        // If the intent DOES NOT contain a donor content URI, then we know that we are
        // creating a new donor.
        if (mCurrentDonorUri == null) {
            // This is a new donor, so change the app bar to say "Add a Donor"
            setTitle(getString(R.string.Editor_activity_title_new_donor));
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing donor, so change app bar to say "Edit Info"
            setTitle(getString(R.string.Editor_activity_title_edit_donor));

            // Initialize a loader to read the donor data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_DONOR_LOADER, null,this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_donor_name);
        mMobileNoEditText = (EditText) findViewById(R.id.edit_donor_mobile);
        mLastDonateEditText = (EditText) findViewById(R.id.edit_donate_date);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mBloodTypeSpinner = (Spinner) findViewById(R.id.spinner_bloodType);
        mButton = (Button) findViewById(R.id.new_donor_add_button);

        //Setting Listener to the editor editText view for observing any changes
        mNameEditText.setOnTouchListener(mTouchListener);
        mMobileNoEditText.setOnTouchListener(mTouchListener);
        mLastDonateEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        mBloodTypeSpinner.setOnTouchListener(mTouchListener);

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


        // Determine if this is a new or existing donor by checking if mCurrentDonorUri is null or not
        if (mCurrentDonorUri == null) {
            // This is a NEW donor, so insert a new donor into the provider,
            // returning the content URI for the new donor.
            Uri newUri = getContentResolver().insert(DonorEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.saving_donor_error_message,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.saving_donor_successful_message,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING donor, so update the donor.
            int rowsAffected = getContentResolver().update(mCurrentDonorUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.updating_donor_error_message),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.updating_donor_successful_message),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new donor, hide the "Delete" menu item.
        if (mCurrentDonorUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the donor hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mDonorHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the donor hasn't changed, continue with handling back button press
        if (!mDonorHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Projection for query method
        String[] projection = {
                DonorEntry._ID,
                DonorEntry.COLUMN_DONOR_NAME,
                DonorEntry.COLUMN_DONOR_MOBILE,
                DonorEntry.COLUMN_DONATE_DATE,
                DonorEntry.COLUMN_BLOOD_GROUP,
                DonorEntry.COLUMN_DONOR_GENDER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentDonorUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of donor attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONOR_NAME);
            int mobileColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONOR_MOBILE);
            int lastDonateColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONATE_DATE);
            int genderColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONOR_GENDER);
            int bloodTypeColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_BLOOD_GROUP);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String mobile = cursor.getString(mobileColumnIndex);
            String lastDonate = cursor.getString(lastDonateColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mMobileNoEditText.setText(mobile);
            mLastDonateEditText.setText(lastDonate);
            int gender = cursor.getInt(genderColumnIndex);
            int blood = cursor.getInt(bloodTypeColumnIndex);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (gender) {
                case DonorEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(0);
                    break;
                case DonorEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(1);
                    break;
            }

            switch (blood) {
                case DonorEntry.A_Positive:
                    mBloodTypeSpinner.setSelection(0);
                    break;
                case DonorEntry.A_Negative:
                    mBloodTypeSpinner.setSelection(1);
                    break;
                case DonorEntry.B_Positive:
                    mBloodTypeSpinner.setSelection(2);
                    break;
                case DonorEntry.B_Negative:
                    mBloodTypeSpinner.setSelection(3);
                    break;
                case DonorEntry.O_Positive:
                    mBloodTypeSpinner.setSelection(4);
                    break;
                case DonorEntry.O_Negative:
                    mBloodTypeSpinner.setSelection(5);
                    break;
                case DonorEntry.AB_Positive:
                    mBloodTypeSpinner.setSelection(6);
                    break;
                case DonorEntry.AB_Negative:
                    mBloodTypeSpinner.setSelection(7);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mMobileNoEditText.setText("");
        mLastDonateEditText.setText("");
        mGenderSpinner.setSelection(0);
        mBloodTypeSpinner.setSelection(0);
    }

    //Show a dialog that warns the user there are unsaved changes that will be lost
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Delete" button, so delete the donor.
                deleteDonor();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteDonor() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentDonorUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentDonorUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_donor_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_donor_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}

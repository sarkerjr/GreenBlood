package com.sarkerjr.greenBlood.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sarkerjr.greenBlood.MainActivity;
import com.sarkerjr.greenBlood.data.BloodContract.DonorEntry;
import com.sarkerjr.greenBlood.R;

public class DonorCursorAdapter extends CursorAdapter {

    public DonorCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView mobileTextView = (TextView) view.findViewById(R.id.mobileNo);
        TextView bloodTypeTextView = (TextView) view.findViewById(R.id.bloodType);
        TextView lastDonateTextView = (TextView) view.findViewById(R.id.donateDate);

        // Find the columns of donor's attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONOR_NAME);
        int mobileColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONOR_MOBILE);
        int bloodTypeColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_BLOOD_GROUP);
        int lastDonateColumnIndex = cursor.getColumnIndex(DonorEntry.COLUMN_DONATE_DATE);

        // Read the donor attributes from the Cursor for the current donor
        String donorName = cursor.getString(nameColumnIndex);
        String donorMobileNo = cursor.getString(mobileColumnIndex);
        //Need to get a int data from a database through string
        int donorBloodType = cursor.getInt(bloodTypeColumnIndex);
        String donorBloodTypeString;
        try {
            donorBloodTypeString = ((MainActivity) context).getBloodTypeString(donorBloodType);
        } catch (ClassCastException e) {
            throw new ClassCastException("Trying to access MainActivity method from different context");
        }
        String donorLastDonate = cursor.getString(lastDonateColumnIndex);

        // Update the TextViews with the attributes for the current donor
        nameTextView.setText(donorName);
        mobileTextView.setText(donorMobileNo);
        bloodTypeTextView.setText(donorBloodTypeString);
        lastDonateTextView.setText(donorLastDonate);
    }
}

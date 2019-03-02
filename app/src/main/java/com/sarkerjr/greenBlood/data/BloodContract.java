package com.sarkerjr.greenBlood.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BloodContract {


    private BloodContract() {}

    public static final String CONTENT_AUTHORITY = "com.sarkerjr.greenBlood";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DONORS = "donors";

    public static final class DonorEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DONORS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DONORS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DONORS;

        // Name of database table for donors
        public final static String TABLE_NAME = "donors";

        // Unique ID number for the donor
        public final static String _ID = BaseColumns._ID;

        //Name of the donors
        public final static String COLUMN_DONOR_NAME ="name";

        //Blood group of donors
        public final static String COLUMN_BLOOD_GROUP = "bloodgroup";

        //Donor Mobile name
        public final static String COLUMN_DONOR_MOBILE = "mobileno";

        //Donor gender
        public final static String COLUMN_DONOR_GENDER = "gender";

        //Donor last blood donating date
        public final static String COLUMN_DONATE_DATE = "lastdonate";

        /**
         * Possible values for blood group types
         */
        public static final int TYPE_UNKNOWN = -1;
        public static final int A_Positive = 0;
        public static final int A_Negative = 1;
        public static final int B_Positive = 2;
        public static final int B_Negative = 3;
        public static final int O_Positive = 4;
        public static final int O_Negative = 5;
        public static final int AB_Positive = 6;
        public static final int AB_Negative = 7;

        /**
         * Possible values for the gender of the donor.
         */
        public static final int GENDER_UNKNOWN = -1;
        public static final int GENDER_MALE = 0;
        public static final int GENDER_FEMALE = 1;

        // Check if the bloodType is valid
        public static boolean isValidBloodType(int blood) {
            if (blood == A_Positive || blood == A_Negative ||
                    blood == B_Positive || blood == B_Negative ||
                    blood == AB_Positive || blood == AB_Negative ||
                    blood == O_Positive || blood == O_Negative ||
                    blood == TYPE_UNKNOWN) {
                return true;
            }
            return false;
        }

        // Check if the gender is valid
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_MALE || gender == GENDER_FEMALE || gender == GENDER_UNKNOWN) {
                return true;
            }
            return false;
        }
    }
}

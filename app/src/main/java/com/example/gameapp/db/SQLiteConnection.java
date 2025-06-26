package com.example.gameapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteConnection {
    private ShownResultsDbHelper dbHelper;
    private SQLiteDatabase db;

    // Constructor that initializes the database helper and gets a writable database instance
    //
    // Parameters:
    //    context (Context): The context used to create the database helper
    public SQLiteConnection(Context context) {
        dbHelper = new ShownResultsDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Inserts a new user into the user table
    //
    // Parameters:
    //    Username (String): The username of the new user
    //    Password (String): The password of the new user
    //
    // Returns:
    //    long: The row ID of the newly inserted user, or -1 if an error occurred
    public long insert_user(String Username, String Password) {
        ContentValues values = new ContentValues();
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME, Username);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_PASSWORD, Password);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_GlASS_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_PAPER_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_ALUMINIUM_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_ELECTRIC_DEVICES_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_BATTERIES_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_CLOTHING_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_OIL_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_PLASTIC_QUANTITY, 0);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USER_POINTS, 0);

        long newRowId = db.insert(ShownResultsContract.ShownResultEntry.TABLE_USER, null, values);
        return newRowId;
    }

    // Inserts a new admin into the admin table
    //
    // Parameters:
    //    Username (String): The username of the new admin
    //    Password (String): The password of the new admin
    //
    // Returns:
    //    long: The row ID of the newly inserted admin, or -1 if an error occurred
    public long insert_admin(String Username, String Password) {
        ContentValues values = new ContentValues();
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_USERNAME, Username);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_PASSWORD, Password);

        long newRowId = db.insert(ShownResultsContract.ShownResultEntry.TABLE_ADMIN, null, values);
        return newRowId;
    }

    // Inserts a new application form into the application form table
    //
    // Parameters:
    //    Username (String): The username of the applicant
    //    GlassItems (int): The quantity of glass items
    //    PaperItems (int): The quantity of paper items
    //    AluminiumItems (int): The quantity of aluminium items
    //    ElectricDeviceItems (int): The quantity of electric device items
    //    Batteries (int): The quantity of batteries
    //    Clothes (int): The quantity of clothes
    //    UsedOilKg (int): The quantity of used oil in kilograms
    //    PlasticItems (int): The quantity of plastic items
    //    Points (int): The number of points
    //
    // Returns:
    //    long: The row ID of the newly inserted application form, or -1 if an error occurred
    public long insert_application_form(String Username,
                                        int GlassItems,
                                        int PaperItems,
                                        int AluminiumItems,
                                        int ElectricDeviceItems,
                                        int Batteries,
                                        int Clothes,
                                        int UsedOilKg,
                                        int PlasticItems,
                                        int Points) {
        ContentValues values = new ContentValues();
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_APPLICANT_USERNAME, Username);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_GLASS_ITEMS, GlassItems);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_PAPER_ITEMS, PaperItems);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_ALUMINIUM_ITEMS, AluminiumItems);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_ELECTRIC_DEVICE_ITEMS, ElectricDeviceItems);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_BATTERIES, Batteries);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_CLOTHES, Clothes);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_USED_OIL_KG, UsedOilKg);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_PLASTIC_ITEMS, PlasticItems);
        values.put(ShownResultsContract.ShownResultEntry.COLUMN_APPLICATION_POINTS, Points);

        long newRowId = db.insert(ShownResultsContract.ShownResultEntry.TABLE_APPLICATION_FORM, null, values);
        return newRowId;
    }

    // Updates a specific column for a user by adding a value to the current value
    //
    // Parameters:
    //    columnName (String): The name of the column to update
    //    Username (String): The username of the user
    //    additionalValue (int): The value to add to the current value
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    private boolean updateUserColumn(String columnName, String Username, int additionalValue) {
        String query = "SELECT " + columnName +
                " FROM " + ShownResultsContract.ShownResultEntry.TABLE_USER +
                " WHERE " + ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Username});
        if (cursor != null && cursor.moveToFirst()) {
            int currentValue = cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
            cursor.close();

            int newTotalValue = currentValue + additionalValue;

            ContentValues values = new ContentValues();
            values.put(columnName, newTotalValue);

            int rowsAffected = db.update(ShownResultsContract.ShownResultEntry.TABLE_USER, values,
                    ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " = ?", new String[]{Username});

            return rowsAffected > 0;
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    // Updates the points for a user by adding to the current points
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalPoints (int): The points to add to the current points
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateUserPoints(String Username, int additionalPoints) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_POINTS, Username, additionalPoints);
    }

    // Updates the quantity of plastic items for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalPlasticItems (int): The plastic items to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updatePlasticItems(String Username, int additionalPlasticItems) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_PLASTIC_QUANTITY, Username, additionalPlasticItems);
    }

    // Updates the quantity of glass items for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalGlassItems (int): The glass items to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateGlassItems(String Username, int additionalGlassItems) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_GlASS_QUANTITY, Username, additionalGlassItems);
    }

    // Updates the quantity of paper items for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalPaperItems (int): The paper items to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updatePaperItems(String Username, int additionalPaperItems) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_PAPER_QUANTITY, Username, additionalPaperItems);
    }

    // Updates the quantity of aluminium items for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalAluminiumItems (int): The aluminium items to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateAluminiumItems(String Username, int additionalAluminiumItems) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_ALUMINIUM_QUANTITY, Username, additionalAluminiumItems);
    }

    // Updates the quantity of electric device items for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalElectricDeviceItems (int): The electric device items to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateElectricDeviceItems(String Username, int additionalElectricDeviceItems) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_ELECTRIC_DEVICES_QUANTITY, Username, additionalElectricDeviceItems);
    }

    // Updates the quantity of batteries for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalBatteries (int): The batteries to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateBatteries(String Username, int additionalBatteries) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_BATTERIES_QUANTITY, Username, additionalBatteries);
    }

    // Updates the quantity of clothes for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalClothes (int): The clothes to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateClothes(String Username, int additionalClothes) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_CLOTHING_QUANTITY, Username, additionalClothes);
    }

    // Updates the quantity of used oil for a user by adding to the current quantity
    //
    // Parameters:
    //    Username (String): The username of the user
    //    additionalUsedOilKg (int): The used oil to add to the current quantity
    //
    // Returns:
    //    boolean: true if the update was successful, false otherwise
    public boolean updateUsedOil(String Username, int additionalUsedOilKg) {
        return updateUserColumn(ShownResultsContract.ShownResultEntry.COLUMN_USER_OIL_QUANTITY, Username, additionalUsedOilKg);
    }

    // Checks if an admin username exists in the admin table
    //
    // Parameters:
    //    admin (String): The admin username to check
    //
    // Returns:
    //    boolean: true if the admin username exists, false otherwise
    public boolean adminUsernameExists(String admin) {
        String q = "SELECT " + ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_USERNAME + " FROM " + ShownResultsContract.ShownResultEntry.TABLE_ADMIN +
                " WHERE " + ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_USERNAME + " = '" + admin + "'";
        Cursor c = db.rawQuery(q, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    // Checks if the password for a given admin username is correct
    //
    // Parameters:
    //    admin (String): The admin username
    //    password (String): The password to check
    //
    // Returns:
    //    boolean: true if the password is correct, false otherwise
    public boolean isAdminPasswordCorrect(String admin, String password) {
        String q = "SELECT " + ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_PASSWORD + " FROM " + ShownResultsContract.ShownResultEntry.TABLE_ADMIN +
                " WHERE " + ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_USERNAME + " = '" + admin + "'";
        Cursor c = db.rawQuery(q, null);

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        int dbpassidx = c.getColumnIndex(ShownResultsContract.ShownResultEntry.COLUMN_ADMIN_PASSWORD);
        String dbpass = c.getString(dbpassidx);

        if (!dbpass.equals(password)) {
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    // Checks if a user username exists in the user table
    //
    // Parameters:
    //    user (String): The user username to check
    //
    // Returns:
    //    boolean: true if the user username exists, false otherwise
    public boolean userUsernameExists(String user) {
        String q = "SELECT " + ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " FROM " + ShownResultsContract.ShownResultEntry.TABLE_USER +
                " WHERE " + ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " = '" + user + "'";
        Cursor c = db.rawQuery(q, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    // Retrieves the user instances for a given username
    //
    // Parameters:
    //    user (String): The username of the user to retrieve
    //
    // Returns:
    //    ContentValues: A ContentValues object containing the user data, or null if the user does not exist
    public ContentValues getUserInstances(String user) {
        String q = "SELECT * FROM " + ShownResultsContract.ShownResultEntry.TABLE_USER +
                " WHERE " + ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " = '" + user + "'";
        Cursor cursor = db.rawQuery(q, null);

        if (cursor != null && cursor.moveToFirst()) {
            ContentValues values = new ContentValues();

            for (String columnName : cursor.getColumnNames()) {
                int columnIndex = cursor.getColumnIndexOrThrow(columnName);
                switch (cursor.getType(columnIndex)) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        values.put(columnName, cursor.getInt(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        values.put(columnName, cursor.getString(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        values.put(columnName, cursor.getFloat(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        values.put(columnName, cursor.getBlob(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        values.putNull(columnName);
                        break;
                }
            }

            cursor.close();
            return values;
        }

        return null;
    }

    // Checks if the password for a given user username is correct
    //
    // Parameters:
    //    user (String): The user username
    //    password (String): The password to check
    //
    // Returns:
    //    boolean: true if the password is correct, false otherwise
    public boolean isUserPasswordCorrect(String user, String password) {
        String q = "SELECT " + ShownResultsContract.ShownResultEntry.COLUMN_USER_PASSWORD + " FROM " + ShownResultsContract.ShownResultEntry.TABLE_USER +
                " WHERE " + ShownResultsContract.ShownResultEntry.COLUMN_USER_USERNAME + " = '" + user + "'";
        Cursor c = db.rawQuery(q, null);

        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        int dbpassidx = c.getColumnIndex(ShownResultsContract.ShownResultEntry.COLUMN_USER_PASSWORD);
        String dbpass = c.getString(dbpassidx);

        if (!dbpass.equals(password)) {
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    // Deletes the first application instance from the application form table
    public void deleteFirstApplicationInstance() {
        String query = "DELETE FROM " + ShownResultsContract.ShownResultEntry.TABLE_APPLICATION_FORM +
                " WHERE " + ShownResultsContract.ShownResultEntry._ID +
                " = (SELECT " + ShownResultsContract.ShownResultEntry._ID +
                " FROM " + ShownResultsContract.ShownResultEntry.TABLE_APPLICATION_FORM + " LIMIT 1)";

        db.execSQL(query);
    }

    // Retrieves the first application instance from the application form table
    //
    // Returns:
    //    ContentValues: A ContentValues object containing the application form data, or null if no data exists
    public ContentValues getFirstApplicationInstance() {
        String query = "SELECT * FROM " + ShownResultsContract.ShownResultEntry.TABLE_APPLICATION_FORM + " LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            ContentValues values = new ContentValues();

            for (String columnName : cursor.getColumnNames()) {
                int columnIndex = cursor.getColumnIndexOrThrow(columnName);
                switch (cursor.getType(columnIndex)) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        values.put(columnName, cursor.getInt(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        values.put(columnName, cursor.getString(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        values.put(columnName, cursor.getFloat(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        values.put(columnName, cursor.getBlob(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        values.putNull(columnName);
                        break;
                }
            }

            cursor.close();
            return values;
        }

        return null;
    }

    // Retrieves the top three users based on their points
    //
    // Returns:
    //    List<ContentValues>: A list of ContentValues objects, each containing data for one of the top three users
    public List<ContentValues> getTopThreeUsers() {
        List<ContentValues> topUsers = new ArrayList<>();
        String query = "SELECT * FROM " + ShownResultsContract.ShownResultEntry.TABLE_USER + " ORDER BY " +
                ShownResultsContract.ShownResultEntry.COLUMN_USER_POINTS + " DESC LIMIT 3";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ContentValues user = new ContentValues();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            user.put(columnName, cursor.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            user.put(columnName, cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            user.put(columnName, cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            user.put(columnName, cursor.getBlob(i));
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            user.putNull(columnName);
                            break;
                    }
                }
                topUsers.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return topUsers;
    }

    // Retrieves the SQLiteDatabase object
    //
    // Returns:
    //    SQLiteDatabase: The SQLiteDatabase object
    public SQLiteDatabase getDB() {
        return this.db;
    }

    // Closes the database connection
    public void close() {
        db.close();
    }
}
